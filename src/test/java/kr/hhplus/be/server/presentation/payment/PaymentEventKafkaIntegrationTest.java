package kr.hhplus.be.server.presentation.payment;

import kr.hhplus.be.server.domain.payment.PaymentCompletedEvent;
import kr.hhplus.be.server.infrastructure.message.PaymentSuccessPayload;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@EmbeddedKafka(partitions = 1, topics = { "payment.success" })
@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.group-id=test-group",
        "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
        "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=*",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentEventKafkaIntegrationTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, PaymentSuccessPayload> testConsumer;

    @BeforeAll
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, PaymentSuccessPayload.class.getName());

        ConsumerFactory<String, PaymentSuccessPayload> cf = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new StringDeserializer(),
                new JsonDeserializer<>(PaymentSuccessPayload.class, false)
        );

        testConsumer = cf.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(testConsumer, "payment.success");
    }

    @AfterAll
    void tearDown() {
        testConsumer.close();
    }

    @Test
    @Transactional
    void 이벤트_발행시_Kafka에_메시지가_전송되고_소비된다() {
        // given
        PaymentCompletedEvent event = new PaymentCompletedEvent(
                1L, 1L, 1L, LocalDateTime.now()
        );

        // when: 트랜잭션 커밋 이후 리스너가 실행됨
        eventPublisher.publishEvent(event);
    }
}