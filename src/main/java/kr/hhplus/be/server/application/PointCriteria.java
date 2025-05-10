package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.presentation.point.object.PointRequest;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PointCriteria(
        UUID uuid,
        Long point
) {
    public static PointCriteria from (PointRequest pointRequest) {
        return PointCriteria.builder()
                .uuid(pointRequest.uuid())
                .point(pointRequest.point())
                .build();
    }

    public PointCommand toPointCommand () {
        return PointCommand.builder().userId(uuid).point(point).build();
    }
}
