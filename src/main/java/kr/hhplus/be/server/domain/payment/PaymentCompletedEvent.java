package kr.hhplus.be.server.domain.payment;

import lombok.Builder;

@Builder
public record PaymentCompletedEvent (
        Long reservationId,
        Long paymentId
) {

}
