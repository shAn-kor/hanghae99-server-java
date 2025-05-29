package kr.hhplus.be.server.domain.payment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PaymentCompletedEvent (
        Long reservationId,
        Long paymentId,
        Long amount,
        LocalDateTime paidAt

) {

}
