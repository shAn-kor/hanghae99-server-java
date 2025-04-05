package kr.hhplus.be.server.presentation.reservation.object;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ReservationRequest(
        @NotNull UUID uuid,
        @NotNull List<Integer> seatList
        ) {
}
