package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.application.dto.ReservationResult;
import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
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
                                .reservationId(reservation.reservationId())
                                .seatId(rI.seatId())
                                .build())
                .toList();

        for (ReservationItem reservationItem : list) {
            reservationRepository.saveItem(reservationItem);
        }
    }

    public List<ReservationItem> getDeadItems(DeadlineItemCriteria deadlineItemCriteria) {
        List<Reservation> deadReservations = reservationRepository.getDeadReservations(deadlineItemCriteria.deadline());

        List<ReservationItem> deadItems = new LinkedList<>();
        for (Reservation reservation : deadReservations) {
            deadItems.addAll(reservation.items());
        }

        return deadItems;
    }

    public ReservationResult getTotalAmount(ReservationIdCommand reservationIdCommand) {
        Reservation reservation = reservationRepository.getReservation(reservationIdCommand.reservationId());
        int itemCount = reservation.items().size();

        return new ReservationResult(itemCount * 500L);
    }

}
