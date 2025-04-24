package kr.hhplus.be.server.presentation.reservation;

import lombok.Builder;

import java.util.List;

@Builder
public record ReservationResponse (
        List<Long> seatIdList
) {
}
