package kr.hhplus.be.server.presentation.point.object;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record PointRequest (
        @NotNull UUID uuid,
        @PositiveOrZero Long point
        ) {
}
