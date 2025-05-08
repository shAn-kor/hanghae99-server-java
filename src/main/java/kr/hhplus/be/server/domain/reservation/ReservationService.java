package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.application.ReservationResult;
import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    @Transactional
    public void reserve(ReservationCommand command) {
        Reservation reservation = reservationRepository.save(command.toReservation());

        List<ReservationItem> list = command.items().stream()
                .map(rI ->
                        ReservationItem.builder()
                                .reservation(reservation)
                                .seatId(rI.seatId())
                                .build())
                .collect(Collectors.toList());

        reservation.setReservationItems(list);

        reservationRepository.save(reservation);
    }

    @Transactional
    public void unReserve(ReservationCommand command) {
        Reservation reservation = reservationRepository.findByUserIdAndConcertScheduleId(command.userId(), command.concertScheduleId()).get(0);
        reservation.setStatus(ReservationStatus.EMPTY);
        reservationRepository.save(reservation);
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
