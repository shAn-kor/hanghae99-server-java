package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.exception.InsufficientBalanceException;
import kr.hhplus.be.server.infrastructure.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    public Point getPointByUserId(PointCommand command) {
        return pointRepository.getPoint(command.userId());
    }

    public void checkPoint (PointCommand command) throws InsufficientBalanceException {
        Point point = getPointByUserId(command);

        if (!point.checkPoint(command.point())) {
            throw new InsufficientBalanceException("잔액이 부족합니다.");
        }
    }

    public void chargePoint(PointCommand command) {
        Point point = getPointByUserId(command);
        Point newPoint = point.charge(command.point());
        pointRepository.save(newPoint);
    }

    public void usePoint(PointCommand command) {
        Point point = getPointByUserId(command);
        Point newPoint = point.use(command.point());
        pointRepository.save(newPoint);
    }
}
