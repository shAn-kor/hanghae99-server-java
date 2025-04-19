package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaSeatRepository extends JpaRepository<Seat, Long> {
    Seat findBySeatNumber(Integer seatNumber);

    List<Seat> findByStatus(SeatStatus status);


    Seat findByConcertScheduleIdAndSeatNumber(Long concertScheduleId, Integer seatNumber);
}
