package kr.hhplus.be.server.presentation.seat.object;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record SeatRequest(
        @NotNull LocalDateTime concertDate,
        @NotNull UUID uuid
        ) {
}
