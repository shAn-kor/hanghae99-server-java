package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.concert.ConcertRankResult;

import java.util.List;

public interface ConcertRankingRepository {
    void save(Long concertId, long secondsSinceOpen);

    List<ConcertRankResult> getRanks();
}
