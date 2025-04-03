package kr.hhplus.be.server.presentation.concert.object;

import jakarta.validation.constraints.NotNull;

public record DateRequest(
        @NotNull Long concertId,
        @NotNull Long hallId
) {
}
