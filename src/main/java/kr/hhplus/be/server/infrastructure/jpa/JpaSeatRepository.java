package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaSeatRepository extends JpaRepository<Seat, Long> {
    Seat findBySeatNumber(Integer seatNumber);

    List<Seat> findByStatus(SeatStatus status);


    Seat findByConcertScheduleIdAndSeatNumber(Long concertScheduleId, Integer seatNumber);

    @Query("select s from seat s where s.venueId = :venueId and s.seatId not in :reservedSeatIds")
    List<Seat> getUnreservedSeats(Long venueId, List<Long> reservedSeatIds);
}
