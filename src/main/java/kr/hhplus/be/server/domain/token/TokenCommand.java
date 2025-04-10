package kr.hhplus.be.server.domain.token;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TokenCommand(
        UUID userId
) {
}
