package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.point.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaPointRepository extends JpaRepository<Point, Long> {
    Point findByUserId(UUID userId);
}
