package kr.hhplus.be.server.presentation.payment.object;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.application.PaymentCriteria;

public record PaymentRequest(
        @NotNull Long reservationId
) {
    public PaymentCriteria toCriteria() {
        return PaymentCriteria.builder().reservationId(reservationId).build();
    }
}
