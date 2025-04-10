package kr.hhplus.be.server.domain.pointhistory;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PointHistory(
        Long historyId,
        Long pointId,
        PointHistoryType type,
        LocalDateTime createdAt
) {
    public PointHistory {
        if (historyId != null && historyId <= 0) {
            throw new IllegalArgumentException("historyId must be greater than 0");
        }

        if (pointId == null) {
            throw new IllegalArgumentException("pointId must not be null");
        }
        if (pointId <= 0) {
            throw new IllegalArgumentException("pointId must be greater than 0");
        }
    }
}
