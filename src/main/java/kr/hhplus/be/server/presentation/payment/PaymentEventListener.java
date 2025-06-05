package kr.hhplus.be.server.presentation.payment;

import kr.hhplus.be.server.domain.payment.PaymentCompletedEvent;
import kr.hhplus.be.server.infrastructure.kafka.KafkaProducer;
import kr.hhplus.be.server.infrastructure.message.PaymentSuccessPayload;
import kr.hhplus.be.server.presentation.KafkaPublishingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {
    private final KafkaProducer kafkaProducer;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PaymentCompletedEvent event) throws KafkaPublishingException {
        PaymentSuccessPayload payload = new PaymentSuccessPayload(event);
        String key = event.paymentId().toString();

        try {
            kafkaProducer.publish("payment.success", key, payload);
        } catch (Exception e) {
            // 로그 기록
            log.error("Kafka publish failed for payment.success (paymentId={}): {}", key, e.getMessage(), e);

            throw new KafkaPublishingException("Kafka 전송 실패로 인해 후속 처리 중단", e);
        }
    }
}
