package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.concertschedule.ConcertSchedule;

import java.util.List;

public interface ConcertScheduleRepository {
    List<ConcertSchedule> getConcertDates(Long concertId);
}
