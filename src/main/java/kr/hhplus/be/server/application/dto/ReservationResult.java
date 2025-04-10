package kr.hhplus.be.server.application.dto;

import lombok.Builder;

@Builder
public record ReservationResult(
        Long totalAmount
) {
}
