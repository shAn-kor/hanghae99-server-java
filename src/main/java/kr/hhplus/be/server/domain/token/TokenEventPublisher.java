package kr.hhplus.be.server.domain.token;

public interface TokenEventPublisher {
    void publish(TokenEvent event);
}
