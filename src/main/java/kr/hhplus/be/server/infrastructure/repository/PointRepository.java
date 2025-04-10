package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.point.Point;

import java.util.UUID;

public interface PointRepository {
    Boolean checkPoint(UUID userId);

    void charge(UUID userId, Long price);

    void use(UUID userId, Long amount);

    Point getPoint(UUID userId);
}
