package kr.hhplus.be.server.infrastructure.repository.impl;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.infrastructure.jpa.JpaConcertRepository;
import kr.hhplus.be.server.infrastructure.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {
    private final JpaConcertRepository jpaConcertRepository;

    @Override
    public List<Concert> concertList() {
        return jpaConcertRepository.findAll();
    }

    @Override
    public Optional<Concert> getConcert(Long concertId) {
        return jpaConcertRepository.findById(concertId);
    }
}
