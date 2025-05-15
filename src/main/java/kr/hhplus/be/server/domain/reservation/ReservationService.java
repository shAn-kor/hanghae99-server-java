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

    @Transactional(readOnly = true)
    public void checkStatus(ReservationIdCommand reservationIdCommand) throws IllegalAccessException {
        Reservation reservation = reservationRepository.getReservation(reservationIdCommand.reservationId());
        reservation.checkReserved();
    }

    @Transactional
    public void endReserve(ReservationIdCommand command) {
        Reservation reservation = reservationRepository.getReservation(command.reservationId());
        reservation.reserve();
        reservationRepository.save(reservation);
    }

    public boolean checkSoldOut(ReservationTotalCommand command) {
        Integer reservationItemCount = 0;

        for (Long concertScheduleId : command.concertScheduleIdList()) {
            List<ReservationItem> reservationItems = reservationRepository.getReservedItems(concertScheduleId);
            reservationItemCount += reservationItems.size();
        }

        return reservationItemCount.equals(command.totalTicketCount());
    }

    public Reservation getReservation(ReservationIdCommand command) {
        return reservationRepository.getReservation(command.reservationId());
    }

}
