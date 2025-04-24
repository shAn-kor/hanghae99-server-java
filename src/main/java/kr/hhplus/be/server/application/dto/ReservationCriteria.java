package kr.hhplus.be.server.application.dto;

import kr.hhplus.be.server.domain.concertschedule.ConcertScheduleCommand;
import kr.hhplus.be.server.domain.reservation.ReservationCommand;
import kr.hhplus.be.server.domain.seat.SeatCommand;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ReservationCriteria(
        UUID uuid,
        Long concertScheduleId,
        List<Integer> seatList
) {
    public ConcertScheduleCommand toConcertScheduleCommand() {
        return ConcertScheduleCommand.builder().concertScheduleId(this.concertScheduleId).build();
    }

    public static ReservationCommand toReservationCommand(ReservationCriteria criteria) {
        return ReservationCommand.builder().userId(criteria.uuid()).concertScheduleId(criteria.concertScheduleId()).build();
    }

    public static SeatCommand toSeatCommand(ReservationCriteria criteria) {
        return SeatCommand.builder().concertScheduleId(criteria.concertScheduleId()).seatNumbers(criteria.seatList).build();
    }
}
