package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.reservation.ReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaReservationItemRepository extends JpaRepository<ReservationItem, Long> {

    List<ReservationItem> findByReservation_ReservationId(Long reservationId);

    @Query("SELECT ri FROM reservation_item ri JOIN ri.reservation r WHERE r.createdAt <= :deadline AND r.status = 'WAITING'")
    List<ReservationItem> getDeadItems(LocalDateTime deadline);

    @Query("select ri from reservation_item ri join ri.reservation r where r.concertScheduleId = :concertScheduleId and (r.status = 'RESERVED' or (r.createdAt >= :deadline and r.status = 'WAITING' ))")
    List<ReservationItem> getReservedItems(@Param("concertScheduleId") Long concertScheduleId, @Param("deadline") LocalDateTime deadline);
}
