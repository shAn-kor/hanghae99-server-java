package kr.hhplus.be.server.domain.reservation;

import lombok.Builder;

@Builder
public record ReservationIdCommand(
        Long reservationId
) {
}
