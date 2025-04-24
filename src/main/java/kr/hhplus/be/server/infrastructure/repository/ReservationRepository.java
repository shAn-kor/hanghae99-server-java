package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    void saveItem(ReservationItem reservationItem);

    List<Reservation> getDeadReservations(LocalDateTime deadline);

    Reservation getReservation(Long aLong);

    List<Reservation> findByUserId(UUID userId);

    List<ReservationItem> getItems(Long reservationId);

    List<ReservationItem> getDeadItems(LocalDateTime deadline);

    List<ReservationItem> getReservedItems(Long concertScheduleId);

    Reservation findByUserIdAndConcertScheduleId(UUID uuid, Long concertScheduleId);
}
