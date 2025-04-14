package kr.hhplus.be.server.domain.token;

import java.time.LocalDateTime;
import java.util.UUID;

public record Token (
        UUID tokenId,
        UUID userId,
        Integer position,
        Boolean valid,
        LocalDateTime createdAt
) {
    public Token updateValidTrue() {
        return new Token(tokenId, userId, position, true, createdAt);
    }
}
