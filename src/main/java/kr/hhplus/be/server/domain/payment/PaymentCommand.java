package kr.hhplus.be.server.domain.payment;

import lombok.Builder;

@Builder
public record PaymentCommand(
        Long reservationId,
        Long amount
) {
}
