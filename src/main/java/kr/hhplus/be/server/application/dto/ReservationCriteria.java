package kr.hhplus.be.server.application.dto;

import kr.hhplus.be.server.domain.reservation.ReservationCommand;
import kr.hhplus.be.server.domain.seat.SeatCommand;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ReservationCriteria(
        UUID uuid,
        Long concertDateTimeId,
        List<Integer> seatList
) {
    public static ReservationCommand toReservationCommand(ReservationCriteria criteria) {
        return ReservationCommand.builder().userId(criteria.uuid()).build();
    }

    public static SeatCommand toSeatCommand(ReservationCriteria criteria) {
        return SeatCommand.builder().concertDateId(criteria.concertDateTimeId()).seatNumbers(criteria.seatList).build();
    }
}
