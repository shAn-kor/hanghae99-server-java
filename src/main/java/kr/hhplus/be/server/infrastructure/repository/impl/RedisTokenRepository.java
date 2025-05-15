package kr.hhplus.be.server.infrastructure.repository.impl;

import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.infrastructure.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Repository
@Primary
@RequiredArgsConstructor
public class RedisTokenRepository implements TokenRepository {
    private final StringRedisTemplate redisTemplate;

    private static final int MAX_ACTIVE = 50;
    private static final Duration USER_TTL = Duration.ofMinutes(5);
    private static final String QUEUE_KEY_PREFIX = "queue:concert:";
    private static final String ACTIVE_KEY_PREFIX = "active:concert:";

    @Override
    public Token getToken(UUID uuid) {
        return null;
    }

    @Override
    public Token save(Token token) {
        return null;
    }

    @Override
    public Integer getMaxPosition() {
        // Redis의 SortedSet에서 마지막 score 조회
        return null; // Redis 기준에선 불필요
    }

    @Override
    public void fillActiveQueue(UUID uuid, Long concertId) {
        String queueKey = QUEUE_KEY_PREFIX + concertId;
        String activeKey = ACTIVE_KEY_PREFIX + concertId;

        Long activeSize = redisTemplate.opsForSet().size(activeKey);
        if (activeSize == null) activeSize = 0L;

        long toFill = MAX_ACTIVE - activeSize;
        if (toFill <= 0) return;

        Set<String> queue = redisTemplate.opsForZSet().range(queueKey, 0, -1);
        if (queue == null) return;

        for (String id : queue) {
            Boolean alreadyActive = redisTemplate.opsForSet().isMember(activeKey, id);
            if (Boolean.TRUE.equals(alreadyActive)) continue;

            redisTemplate.opsForZSet().remove(queueKey, id);
            redisTemplate.opsForSet().add(activeKey, id);

            if (--toFill == 0) break;
        }
    }

    @Override
    public void endActiveToken(UUID userId, Long concertId) {
        String activeKey = ACTIVE_KEY_PREFIX + concertId;
        redisTemplate.opsForSet().remove(activeKey, userId.toString());
    }

    @Override
    public Token getToken(UUID userId, Long concertId) {
        String queueKey = QUEUE_KEY_PREFIX + concertId;
        String infoKey = queueKey + ":info:" + userId;

        Double score = redisTemplate.opsForZSet().score(queueKey, userId.toString());
        if (score == null) return null;

        String createdAtStr = redisTemplate.opsForValue().get(infoKey);
        LocalDateTime createdAt = (createdAtStr != null) ? LocalDateTime.parse(createdAtStr) : LocalDateTime.now();

        return Token.builder()
                .userId(userId)
                .concertId(concertId)
                .position(score.intValue())
                .createdAt(createdAt)
                .valid(false)
                .build();
    }

    @Override
    public Token generateToken(UUID userId, Long concertId) {
        String queueKey = QUEUE_KEY_PREFIX + concertId;
        String infoKey = queueKey + ":info:" + userId;
        String activeKey = ACTIVE_KEY_PREFIX + concertId;

        // 중복 방지
        if (redisTemplate.opsForZSet().score(queueKey, userId.toString()) != null) {
            return getToken(userId, concertId);
        }

        long position = redisTemplate.opsForZSet().zCard(queueKey) + 1;

        // 활성 대기열에 빈 자리 있으면 바로 등록
        Long activeSize = redisTemplate.opsForSet().size(activeKey);
        if (activeSize == null || activeSize < MAX_ACTIVE) {
            redisTemplate.opsForSet().add(activeKey, userId.toString());
        } else {
            redisTemplate.opsForZSet().add(queueKey, userId.toString(), position);
            redisTemplate.opsForValue().set(infoKey, LocalDateTime.now().toString(), USER_TTL);
        }

        return Token.builder()
                .userId(userId)
                .concertId(concertId)
                .position((int) position)
                .createdAt(LocalDateTime.now())
                .valid(false)
                .build();
    }

    @Override
    public void endToken(UUID userId, Long concertId) {
        String queueKey = QUEUE_KEY_PREFIX + concertId;
        String infoKey = queueKey + ":info:" + userId;

        redisTemplate.opsForZSet().remove(queueKey, userId.toString());
        redisTemplate.delete(infoKey);
    }
}
