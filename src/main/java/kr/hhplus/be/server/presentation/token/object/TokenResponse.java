package kr.hhplus.be.server.presentation.token.object;

import kr.hhplus.be.server.domain.token.Token;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TokenResponse (
    UUID tokenId,
    UUID userId,
    Integer position,
    Boolean valid,
    LocalDateTime createdAt
) {
    public static TokenResponse from (Token token) {
        return TokenResponse.builder()
                .tokenId(token.getTokenId())
                .userId(token.getUserId())
                .position(token.getPosition())
                .valid(token.getValid())
                .createdAt(token.getCreatedAt())
                .build();
    }
}
