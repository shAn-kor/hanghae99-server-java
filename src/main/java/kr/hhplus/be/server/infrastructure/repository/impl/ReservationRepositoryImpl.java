package kr.hhplus.be.server.infrastructure.repository.impl;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationItem;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.infrastructure.jpa.JpaReservationItemRepository;
import kr.hhplus.be.server.infrastructure.jpa.JpaReservationRepository;
import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
    private final JpaReservationRepository jpaReservationRepository;
    private final JpaReservationItemRepository jpaReservationItemRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return jpaReservationRepository.save(reservation);
    }

    @Override
    public void saveItem(ReservationItem reservationItem) {
        jpaReservationItemRepository.save(reservationItem);
    }

    @Override
    public List<Reservation> getDeadReservations(LocalDateTime deadline) {
        return jpaReservationRepository.findByCreatedAtBeforeAndStatusNot(deadline, ReservationStatus.RESERVED);
    }

    @Override
    public Reservation getReservation(Long reservationId) {
        return jpaReservationRepository.findById(reservationId).orElse(null);
    }

    @Override
    public List<Reservation> findByUserId(UUID userId) {
        return jpaReservationRepository.findByUserId(userId);
    }

    @Override
    public List<ReservationItem> getItems(Long reservationId) {
        return jpaReservationItemRepository.findByReservationId(reservationId);
    }

    @Override
    public List<ReservationItem> getDeadItems(LocalDateTime deadline) {
        return jpaReservationItemRepository.getDeadItems(deadline);
    }

    @Override
    public List<ReservationItem> getReservedItems(Long concertScheduleId) {
        return jpaReservationItemRepository.getReservedItems(concertScheduleId);
    }

    @Override
    public Reservation findByUserIdAndConcertScheduleId(UUID uuid, Long concertScheduleId) {
        return jpaReservationRepository.findByUserIdAndConcertScheduleId(uuid, concertScheduleId);
    }
}
