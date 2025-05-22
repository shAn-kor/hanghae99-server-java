package kr.hhplus.be.server.infrastructure.event;

import kr.hhplus.be.server.domain.payment.PaymentCompletedEvent;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringPaymentEventPublisher implements PaymentEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void success(PaymentCompletedEvent event) {
        eventPublisher.publishEvent(event);
    }
}
