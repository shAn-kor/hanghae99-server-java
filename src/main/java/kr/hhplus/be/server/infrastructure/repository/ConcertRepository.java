package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.concert.Concert;

import java.util.List;

public interface ConcertRepository {
    List<Concert> concertList();
}
