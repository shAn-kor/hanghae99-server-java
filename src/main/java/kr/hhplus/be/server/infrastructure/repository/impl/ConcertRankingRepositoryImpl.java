package kr.hhplus.be.server.infrastructure.repository.impl;

import kr.hhplus.be.server.domain.concert.ConcertRankResult;
import kr.hhplus.be.server.infrastructure.repository.ConcertRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ConcertRankingRepositoryImpl implements ConcertRankingRepository {
    private final StringRedisTemplate redisTemplate;
    private static final String RANKING_KEY = "concert:soldout:ranking";

    @Override
    public void save(Long concertId, long secondsSinceOpen) {
        redisTemplate.opsForZSet().add(RANKING_KEY, concertId.toString(), secondsSinceOpen);
    }

    @Override
    public List<ConcertRankResult> getRanks() {
        Set<ZSetOperations.TypedTuple<String>> rankSet =
                redisTemplate.opsForZSet().rangeWithScores(RANKING_KEY, 0, -1);

        if (rankSet == null) return List.of();

        return rankSet.stream()
                .map(tuple -> new ConcertRankResult(
                        Long.parseLong(tuple.getValue()),    // concertId
                        tuple.getScore().longValue()))       // seconds since open
                .toList();
    }
}
