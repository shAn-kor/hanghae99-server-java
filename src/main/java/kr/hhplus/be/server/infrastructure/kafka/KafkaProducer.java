package kr.hhplus.be.server.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void publish(String topic, String key, T payload) {
        try {
            kafkaTemplate.send(topic, key, payload)
                    .thenAccept(result -> log.info("✅ Kafka 메시지 전송 성공. topic={}, key={}, value={}, offset={}",
                            topic, key, payload, result.getRecordMetadata().offset()))
                    .exceptionally(ex -> {
                        log.error("❌ Kafka 메시지 전송 실패. topic={}, key={}, value={}, error={}",
                                topic, key, payload, ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            log.error("❗ Kafka 전송 중 예외 발생. topic={}, key={}, value={}", topic, key, payload, e);
            throw e;
        }
    }
}
