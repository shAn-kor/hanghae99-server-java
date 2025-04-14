package kr.hhplus.be.server.domain.point;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PointCommand(
        UUID userId,
        @Positive
        @NotNull
        Long point
) {

}
