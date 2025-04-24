package kr.hhplus.be.server.infrastructure.repository.impl;

import kr.hhplus.be.server.domain.concertschedule.ConcertSchedule;
import kr.hhplus.be.server.infrastructure.jpa.JpaConcertScheduleRepository;
import kr.hhplus.be.server.infrastructure.repository.ConcertScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertScheduleRepositoryImpl implements ConcertScheduleRepository {
    private final JpaConcertScheduleRepository jpaConcertScheduleRepository;

    @Override
    public List<ConcertSchedule> getConcertDates(Long concertId) {
        return jpaConcertScheduleRepository.findByConcert_ConcertId(concertId);
    }

    @Override
    public ConcertSchedule getConcertSchedule(Long concertScheduleId) {
        return jpaConcertScheduleRepository.findById(concertScheduleId).orElse(null);
    }
}
