package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.application.dto.ReservationResult;
import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public void reserve(ReservationCommand command) {
        Reservation reservation = reservationRepository.save(command.toReservation());

        List<ReservationItem> list = command.items().stream()
                .map(rI ->
                        ReservationItem.builder()
                                .reservation(reservation)
                                .seatId(rI.seatId())
                                .build())
                .toList();

        reservation.setReservationItems(list);
        reservationRepository.save(reservation);
    }

    public void unReserve(ReservationCommand command) {
        Reservation reservation = reservationRepository.findByUserIdAndConcertScheduleId(command.userId(), command.concertScheduleId());
        reservation.setStatus(ReservationStatus.EMPTY);
        reservationRepository.save(reservation);
    }

    @Scheduled(fixedRate = 60000)
    public void autoCancelReservation() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(3);
        List<Reservation> expiredReservations = reservationRepository.getDeadReservations(deadline);
        expiredReservations.forEach(reservation -> {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
        });
    }

    public List<ReservationItem> getDeadItems(DeadlineItemCriteria deadlineItemCriteria) {
        return reservationRepository.getDeadItems(deadlineItemCriteria.deadline());
    }

    @Transactional(readOnly = true)
    public ReservationResult getTotalAmount(ReservationIdCommand reservationIdCommand) {
        Reservation reservation = reservationRepository.getReservation(reservationIdCommand.reservationId());
        int itemCount = reservation.getReservationItems().size();

        return new ReservationResult(itemCount * 500L);
    }

    public List<ReservationItem> getReservedItems(ReservationCommand command) {
        return reservationRepository.getReservedItems(command.concertScheduleId());
    }
}
