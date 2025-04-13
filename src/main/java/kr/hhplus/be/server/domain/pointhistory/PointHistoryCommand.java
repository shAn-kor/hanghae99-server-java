package kr.hhplus.be.server.domain.pointhistory;

import lombok.Builder;

@Builder
public record PointHistoryCommand(
        Long pointId,
        PointHistoryType type
) {
}
