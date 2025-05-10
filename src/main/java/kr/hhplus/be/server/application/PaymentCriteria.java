package kr.hhplus.be.server.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentCriteria(
        UUID userId,
        Long reservationId
) {
}
