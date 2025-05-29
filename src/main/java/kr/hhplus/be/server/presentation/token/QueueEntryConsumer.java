package kr.hhplus.be.server.presentation.token;

import kr.hhplus.be.server.domain.token.TokenCommand;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.presentation.token.object.QueueEntryPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueEntryConsumer {
    private final TokenService tokenService;

    @KafkaListener(
            topics = "queue.entry",
            groupId = "queue-entry-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void listen(ConsumerRecord<String, QueueEntryPayload> record, Acknowledgment ack) {
        QueueEntryPayload payload = record.value();
        log.info("▶️ Kafka 수신: {}", payload);

        // Redis 대기열에 추가
        tokenService.generateToken(TokenCommand.builder().userId(payload.getUserId()).concertId(payload.getConcertId()).build());

        ack.acknowledge(); // 수동 커밋
    }
}
