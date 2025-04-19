package kr.hhplus.be.server.infrastructure.repository.impl;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.infrastructure.jpa.JpaPointRepository;
import kr.hhplus.be.server.infrastructure.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {
    private final JpaPointRepository jpaPointRepository;

    @Override
    public Point getPoint(UUID userId) {
        return jpaPointRepository.findByUserId(userId);
    }

    @Override
    public void save(Point point) {
        jpaPointRepository.save(point);
    }
}
