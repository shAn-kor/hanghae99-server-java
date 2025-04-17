package kr.hhplus.be.server.domain.point;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PointCommand(
        UUID userId,
        Long point
) {

}
