package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.reservation.ReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaReservationItemRepository extends JpaRepository<ReservationItem, Long> {

    List<ReservationItem> findByReservationId(Long reservationId);

    @Query("SELECT ri FROM reservation_item ri JOIN reservation r ON ri.reservationId = r.reservationId WHERE r.createdAt <= :deadline AND r.status = 'WAITING'")
    List<ReservationItem> getDeadItems(LocalDateTime deadline);
}
