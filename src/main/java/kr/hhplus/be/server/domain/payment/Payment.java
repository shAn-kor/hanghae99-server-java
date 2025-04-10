package kr.hhplus.be.server.domain.payment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Payment(
        Long paymentId,
        Long reservationId,
        Long amount,
        LocalDateTime paidAt
) {
}
