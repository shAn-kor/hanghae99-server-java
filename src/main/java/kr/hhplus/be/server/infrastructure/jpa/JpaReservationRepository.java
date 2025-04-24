package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JpaReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCreatedAtBeforeAndStatusNot(LocalDateTime deadLine, ReservationStatus status);

    List<Reservation> findByUserId(UUID userId);

    Reservation findByUserIdAndConcertScheduleId(UUID uuid, Long concertScheduleId);
}
