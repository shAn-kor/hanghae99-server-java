package kr.hhplus.be.server.infrastructure.message;

import kr.hhplus.be.server.domain.token.TokenEvent;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TokenCreatePayload(
        UUID userId,
        Long concertId
) {
    public static TokenCreatePayload of (TokenEvent tokenEvent) {
        return new TokenCreatePayload(tokenEvent.userId(), tokenEvent.concertId());
    }
}
