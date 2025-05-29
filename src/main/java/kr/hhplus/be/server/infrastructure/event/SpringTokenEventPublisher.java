package kr.hhplus.be.server.infrastructure.event;

import kr.hhplus.be.server.domain.token.TokenEvent;
import kr.hhplus.be.server.domain.token.TokenEventPublisher;
import kr.hhplus.be.server.infrastructure.kafka.KafkaProducer;
import kr.hhplus.be.server.infrastructure.message.TokenCreatePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringTokenEventPublisher implements TokenEventPublisher {
    private final KafkaProducer kafkaProducer;

    @Override
    public void publish(TokenEvent event) {
        TokenCreatePayload payload = TokenCreatePayload.of(event);
        kafkaProducer.publish("queue.entry", event.userId() + ":" + event.concertId(), payload);
    }
}
