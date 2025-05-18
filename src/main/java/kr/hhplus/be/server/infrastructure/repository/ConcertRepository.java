package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.concert.Concert;

import java.util.List;
import java.util.Optional;

public interface ConcertRepository {
    List<Concert> concertList();

    Optional<Concert> getConcert(Long concertId);
}
