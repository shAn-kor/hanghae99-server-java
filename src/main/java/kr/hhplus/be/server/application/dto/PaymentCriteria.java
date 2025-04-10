package kr.hhplus.be.server.application.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentCriteria(
        UUID userId,
        Long reservationId
) {
}
