package kr.hhplus.be.server.domain.payment;

public interface PaymentEventPublisher {
    void success (PaymentCompletedEvent event);
}
