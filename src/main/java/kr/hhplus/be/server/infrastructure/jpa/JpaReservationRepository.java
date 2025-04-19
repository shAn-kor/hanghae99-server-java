package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JpaReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCreatedAtBefore(LocalDateTime deadLine);

    List<Reservation> findByUserId(UUID userId);
}
