package kr.hhplus.be.server.config.kafka;

import kr.hhplus.be.server.presentation.token.object.QueueEntryPayload;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.transaction.KafkaTransactionManager;

@Configuration
public class KafkaListenerConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, QueueEntryPayload> kafkaListenerContainerFactory(
            ConsumerFactory<String, QueueEntryPayload> consumerFactory,
            KafkaTransactionManager<Object, Object> kafkaTransactionManager
    ) {
        ConcurrentKafkaListenerContainerFactory<String, QueueEntryPayload> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD); // 수동 커밋
        factory.getContainerProperties().setTransactionManager(kafkaTransactionManager);
        return factory;
    }

    @Bean
    public KafkaTransactionManager<String, QueueEntryPayload> kafkaTransactionManager(
            ProducerFactory<String, QueueEntryPayload> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }
}
