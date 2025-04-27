package kr.hhplus.be.server.application;

import lombok.Builder;

import java.util.List;

@Builder
public record ReservationItemResult(
        List<Long> seatIdList
) {
}
