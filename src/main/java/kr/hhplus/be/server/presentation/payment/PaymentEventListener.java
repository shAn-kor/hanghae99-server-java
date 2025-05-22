package kr.hhplus.be.server.presentation.payment;

import kr.hhplus.be.server.domain.payment.PaymentCompletedEvent;
import kr.hhplus.be.server.infrastructure.message.DataPlatformSendService;
import kr.hhplus.be.server.infrastructure.message.PaymentSuccessPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final DataPlatformSendService dataPlatformSendService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PaymentCompletedEvent event) {
        PaymentSuccessPayload payload = new PaymentSuccessPayload(event);
        dataPlatformSendService.send(payload);
    }
}
