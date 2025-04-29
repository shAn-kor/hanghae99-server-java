package kr.hhplus.be.server.application;

import lombok.Builder;

@Builder
public record ReservationResult(
        Long totalAmount
) {
}
