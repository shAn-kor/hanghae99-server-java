package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.exception.InsufficientBalanceException;
import kr.hhplus.be.server.infrastructure.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    @Transactional(readOnly = true)
    public Point getPointByUserId(PointCommand command) {
        return pointRepository.getPoint(command.userId());
    }

    @Transactional(readOnly = true)
    public void checkPoint (PointCommand command) throws InsufficientBalanceException {
        Point point = getPointByUserId(command);

        if (point.checkPoint(command.point())) {
            throw new InsufficientBalanceException("잔액이 부족합니다.");
        }
    }

    @Transactional
    public void chargePoint(PointCommand command) {
        Point point = getPointByUserId(command);
        point.charge(command.point());
        pointRepository.save(point);
    }

    @Transactional
    public void usePoint(PointCommand command) {
        Point point = getPointByUserId(command);
        point.use(command.point());
        pointRepository.save(point);
    }
}
