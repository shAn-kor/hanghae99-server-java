package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.concertschedule.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaConcertScheduleRepository extends JpaRepository<ConcertSchedule, Long> {
    List<ConcertSchedule> findByConcert_ConcertId(Long concertId);
}
