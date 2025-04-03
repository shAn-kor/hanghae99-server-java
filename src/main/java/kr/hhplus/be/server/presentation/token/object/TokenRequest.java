package kr.hhplus.be.server.presentation.token.object;

import jakarta.validation.constraints.NotNull;

public record TokenRequest(
        @NotNull String token
) {
}
