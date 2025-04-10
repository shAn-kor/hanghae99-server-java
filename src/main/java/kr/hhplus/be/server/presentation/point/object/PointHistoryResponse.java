package kr.hhplus.be.server.presentation.point.object;

import kr.hhplus.be.server.domain.pointhistory.PointHistoryType;

import java.time.LocalDateTime;

public record PointHistoryResponse(
        Long pointId,
        PointHistoryType type,
        LocalDateTime createdAt
) {
}
