package kr.hhplus.be.server.infrastructure.message;

import kr.hhplus.be.server.domain.payment.PaymentCompletedEvent;

public record PaymentSuccessPayload(PaymentCompletedEvent event) {
}
