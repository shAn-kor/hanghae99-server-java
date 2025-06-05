package kr.hhplus.be.server.domain.token;

import kr.hhplus.be.server.presentation.KafkaPublishingException;

public interface TokenEventPublisher {
    void publish(TokenEvent event) throws KafkaPublishingException;
}
