package kr.hhplus.be.server.presentation.point.object;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import kr.hhplus.be.server.application.dto.PointCriteria;

import java.util.UUID;

public record PointRequest (
        @NotNull UUID uuid,
        @PositiveOrZero Long point
        ) {

        public PointCriteria toCriteria() {
                return PointCriteria.builder().uuid(uuid).point(point).build();
        }
}
