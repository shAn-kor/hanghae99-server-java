package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.reservation.ReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaReservationItemRepository extends JpaRepository<ReservationItem, Long> {

    List<ReservationItem> findByReservationId(Long reservationId);
}
