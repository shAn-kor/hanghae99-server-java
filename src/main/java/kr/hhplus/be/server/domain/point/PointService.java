package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.exception.InsufficientBalanceException;
import kr.hhplus.be.server.infrastructure.lock.DistributedLock;
import kr.hhplus.be.server.infrastructure.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "userPoint", key = "#command.userId()", cacheManager = "cacheManager")
    public Point getPointByUserId(PointCommand command) {
        return pointRepository.getPoint(command.userId());
    }

    @Transactional(readOnly = true)
    public void checkPoint (PointCommand command) throws InsufficientBalanceException {
        Point point = pointRepository.getPoint(command.userId());

        if (point.checkPoint(command.point())) {
            throw new InsufficientBalanceException("잔액이 부족합니다.");
        }
    }

    @Transactional
    @CacheEvict(value = "userPoint", key = "#command.userId()")
    @DistributedLock(prefix = "Point:Charge", key = "#command.pointId()")
    public void chargePoint(PointCommand command) {
        Point point = pointRepository.getPoint(command.userId());
        point.charge(command.point());
        pointRepository.save(point);
    }

    @Transactional
    @CacheEvict(value = "userPoint", key = "#command.userId()")
    @DistributedLock(prefix = "Point:Charge", key = "#command.pointId()")
    public void usePoint(PointCommand command) {
        Point point = pointRepository.getPoint(command.userId());
        point.use(command.point());
        pointRepository.save(point);
    }
}
