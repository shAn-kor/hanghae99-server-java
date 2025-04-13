package kr.hhplus.be.server.application;

import kr.hhplus.be.server.presentation.point.object.PointResponse;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PointResult(
        UUID uuid,
        Long point
) {
    public static PointResponse toPointResponse(PointResult pointResult) {
        return PointResponse.builder().uuid(pointResult.uuid()).point(pointResult.point()).build();
    }
}
