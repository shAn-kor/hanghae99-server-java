package kr.hhplus.be.server.application;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TokenResult(
        UUID userId,
        Integer position,
        Boolean valid,
        LocalDateTime createdAt
) {
}
