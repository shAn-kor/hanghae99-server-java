package kr.hhplus.be.server.domain.reservation;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.application.dto.ReservationResult;
import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final EntityManager entityManager;

    public void reserve(ReservationCommand command) {
        Reservation reservation = reservationRepository.save(command.toReservation());
        entityManager.flush(); // ID가 바로 생성되도록 함

        List<ReservationItem> list = command.items().stream()
                .map(rI ->
                        ReservationItem.builder()
                                .reservationId(reservation.getReservationId())
                                .seatId(rI.seatId())
                                .build())
                .toList();

        list.forEach(reservationRepository::saveItem);
    }

    public List<ReservationItem> getDeadItems(DeadlineItemCriteria deadlineItemCriteria) {
        List<Reservation> deadReservations = reservationRepository.getDeadReservations(deadlineItemCriteria.deadline());

        List<ReservationItem> deadItems = new LinkedList<>();
        for (Reservation reservation : deadReservations) {
            List<ReservationItem> deadReservationItems = reservationRepository.getItems(reservation.getReservationId());
            deadItems.addAll(deadReservationItems);
        }

        return deadItems;
    }

    @Transactional(readOnly = true)
    public ReservationResult getTotalAmount(ReservationIdCommand reservationIdCommand) {
        Reservation reservation = reservationRepository.getReservation(reservationIdCommand.reservationId());
        int itemCount = reservationRepository.getItems(reservation.getReservationId()).size();

        return new ReservationResult(itemCount * 500L);
    }

}
