package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.point.Point;

import java.util.UUID;

public interface PointRepository {
    Point getPoint(UUID userId);

    void save(Point point);
}
