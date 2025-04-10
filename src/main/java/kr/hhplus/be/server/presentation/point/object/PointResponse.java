package kr.hhplus.be.server.presentation.point.object;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PointResponse (UUID uuid, Long point) {
}
