package kr.hhplus.be.server.domain.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisTokenService implements TokenService {
    private final StringRedisTemplate redisTemplate;

    private static final int MAX_ACTIVE = 50;
    private static final Duration USER_TTL = Duration.ofMinutes(10);
    private static final String QUEUE_KEY_PREFIX = "queue:concert:";
    private static final String ACTIVE_PREFIX = "active:concert:";


    @Override
    @Transactional
    public Token generateToken(TokenCommand command) {
        UUID userId = command.userId();
        Long concertId = command.concertId();

        String queueKey = QUEUE_KEY_PREFIX + concertId;
        String setKey = queueKey + ":set";
        String infoKey = queueKey + ":info:" + userId;
        String activeKey = ACTIVE_PREFIX + ":" + concertId + ":";

        // 중복 체크
        Boolean alreadyInQueue = redisTemplate.opsForSet().isMember(setKey, command.concertId());
        if (alreadyInQueue) {
            return this.getToken(command);
        }


        Long queueSize = redisTemplate.opsForList().size(queueKey);
        int nextPosition = (queueSize != null) ? queueSize.intValue() : 1;

        // 활성 대기열 자리가 남아 있다면 등록
        Long activeSize = redisTemplate.opsForSet().size(activeKey);
        if (activeSize == null || activeSize < MAX_ACTIVE) {
            redisTemplate.opsForSet().add(activeKey, userId.toString());
        } else {
            // 새로 대기열 진입 → Redis 등록
            redisTemplate.opsForList().rightPush(queueKey, userId.toString());
            redisTemplate.opsForSet().add(setKey, userId.toString());
            redisTemplate.opsForValue().set(infoKey, LocalDateTime.now().toString(), USER_TTL);
        }

        return Token.builder()
                .userId(userId)
                .concertId(concertId)
                .position(nextPosition)
                .valid(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Token getToken(TokenCommand tokenCommand) {
        UUID userId = tokenCommand.userId();
        Long concertId = tokenCommand.concertId();

        String queueKey = "queue:concert:" + concertId;
        String infoKey = queueKey + ":info:" + userId;

        // Redis에 대기 정보가 없으면 null 반환 또는 예외 처리
        Boolean isQueued = redisTemplate.opsForSet().isMember(queueKey + ":set", userId);
        if (Boolean.FALSE.equals(isQueued)) {
            throw new IllegalStateException("대기열에 존재하지 않는 사용자입니다.");
        }

        // createdAt 가져오기
        String createdAtStr = redisTemplate.opsForValue().get(infoKey);
        LocalDateTime createdAt = (createdAtStr != null)
                ? LocalDateTime.parse(createdAtStr)
                : LocalDateTime.now(); // fallback

        // 대기 순서 계산
        List<String> queue = redisTemplate.opsForList().range(queueKey, 0, -1);
        int position = queue != null ? queue.indexOf(userId) + 1 : -1;

        // Redis 기반 임시 Token 객체 생성 (DB 저장은 하지 않음)
        return Token.builder()
                .userId(userId)
                .concertId(concertId)
                .position(position)
                .createdAt(createdAt)
                .valid(false)
                .build();
    }

    @Override
    @Transactional
    public void isValid(TokenCommand command) throws AccessDeniedException {
        UUID userId = command.userId();
        Long concertId = command.concertId();

        String activeKey = "active:concert:" + concertId;

        boolean isActive = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(activeKey, userId.toString()));
        if (!isActive) {
            throw new AccessDeniedException("활성 대기열에 포함되지 않은 사용자입니다.");
        }
    }

    @Override
    @Transactional
    public void endToken(TokenCommand command) {
        UUID userId = command.userId();
        Long concertId = command.concertId();

        String queueKey = QUEUE_KEY_PREFIX + concertId;
        String setKey = queueKey + ":set";
        String infoKey = queueKey + ":info:" + userId;

        redisTemplate.opsForList().remove(queueKey, 1, userId);
        redisTemplate.opsForSet().remove(setKey, userId);
        redisTemplate.delete(infoKey);
    }

    @Override
    @Transactional
    public void fillActiveQueue(TokenCommand command) {
        Long concertId = command.concertId();

        String queueKey = QUEUE_KEY_PREFIX + concertId;
        String activeKey = ACTIVE_PREFIX + ":" + concertId + ":";

        Long activeSize = redisTemplate.opsForSet().size(activeKey);
        if (activeSize == null) activeSize = 0L;

        long toFill = MAX_ACTIVE - activeSize;
        if (toFill <= 0) return;

        List<String> queue = redisTemplate.opsForList().range(queueKey, 0, -1);
        if (queue == null) return;

        for (String id : queue) {
            UUID uuid = UUID.fromString(id);
            if (redisTemplate.opsForSet().isMember(activeKey, uuid)) continue;

            // 전체 대기열에서 제거
            redisTemplate.opsForList().remove(queueKey, 1, id);

            redisTemplate.opsForSet().add(activeKey, id);
            if (--toFill == 0) break;
        }
    }

    @Override
    @Transactional
    public void endActiveToken(TokenCommand command) {
        UUID userId = command.userId();
        Long concertId = command.concertId();
        String activeKey = ACTIVE_PREFIX + ":" + concertId + ":";

        redisTemplate.opsForSet().remove(activeKey, userId);
    }
}
