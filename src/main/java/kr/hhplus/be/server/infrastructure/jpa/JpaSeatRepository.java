package kr.hhplus.be.server.infrastructure.jpa;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaSeatRepository extends JpaRepository<Seat, Long> {
    Seat findBySeatNumber(Integer seatNumber);


    @Query("select s from seat s where s.venueId = :venueId and s.seatId not in :reservedSeatIds")
    List<Seat> getUnreservedSeats(Long venueId, List<Long> reservedSeatIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM seat s WHERE s.venueId = :venueId AND s.seatId IN :seatIds")
    List<Seat> findWithPessimisticLock(@Param("venueId") Long venueId, @Param("seatIds") List<Long> seatIds);
}
