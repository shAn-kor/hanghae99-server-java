package kr.hhplus.be.server.presentation.token.object;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TokenRequest(
        @NotNull UUID userId
) {
}
