package kr.hhplus.be.server.presentation.payment.object;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull Long reservationId
) {
}
