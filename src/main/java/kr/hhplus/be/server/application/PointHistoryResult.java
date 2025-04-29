package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.pointhistory.PointHistoryType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PointHistoryResult(
        Long pointId,
        PointHistoryType type,
        LocalDateTime createdAt
) {
}
