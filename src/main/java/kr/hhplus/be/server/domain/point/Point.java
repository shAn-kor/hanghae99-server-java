package kr.hhplus.be.server.domain.point;

import java.util.UUID;

public record Point(
        Long pointId,
        UUID userId,
        Long balance
) {
    public Point {
        if (pointId != null && pointId <= 0) {
            throw new IllegalArgumentException("pointId must be greater than 0");
        }

        if (userId == null) {
            throw new IllegalArgumentException("userId must be greater than 0");
        }

        if (balance == null || balance <= 0) {
            throw new IllegalArgumentException("balance must be greater than 0");
        }
    }

    public Boolean checkPoint(Long amount) {
        return amount <= balance;
    }
}
