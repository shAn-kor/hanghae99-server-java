package kr.hhplus.be.server.infrastructure.event;

import kr.hhplus.be.server.domain.token.TokenEvent;
import kr.hhplus.be.server.domain.token.TokenEventPublisher;
import kr.hhplus.be.server.infrastructure.kafka.KafkaProducer;
import kr.hhplus.be.server.infrastructure.message.TokenCreatePayload;
import kr.hhplus.be.server.presentation.KafkaPublishingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpringTokenEventPublisher implements TokenEventPublisher {
    private final KafkaProducer kafkaProducer;

    @Override
    public void publish(TokenEvent event) throws KafkaPublishingException {
        TokenCreatePayload payload = TokenCreatePayload.of(event);
        String key = event.userId() + ":" + event.concertId();

        try {
            kafkaProducer.publish("queue.entry", key, payload);
        } catch (Exception e) {
            log.error("Kafka publish failed for queue.entry (userId={}, concertId={}): {}", event.userId(), event.concertId(), e.getMessage(), e);
            // 실패 대응 전략에 따라 선택적으로 예외 재던지기
            throw new KafkaPublishingException("Kafka 발행 실패", e);
        }
    }
}
