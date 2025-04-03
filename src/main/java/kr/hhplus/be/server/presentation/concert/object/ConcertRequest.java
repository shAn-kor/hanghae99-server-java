package kr.hhplus.be.server.presentation.concert.object;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ConcertRequest(
        @NotNull Long concertId,
        @NotNull Long hallId,
        @NotNull LocalDateTime date
) {
}
