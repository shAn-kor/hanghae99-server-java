package kr.hhplus.be.server.domain.reservation;

import lombok.Builder;

import java.util.List;

@Builder
public record ReservationTotalCommand (
        List<Long> concertScheduleIdList,
        Integer totalTicketCount
) {
}
