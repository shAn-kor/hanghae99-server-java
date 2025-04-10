package kr.hhplus.be.server.domain.point;

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

    public Boolean checkPoint (PointCommand command) {
        Point point = getPointByUserId(command);
        return point.checkPoint(command.point());
    }

    public void chargePoint(PointCommand command) {
        pointRepository.charge(command.userId(), command.point());
    }

    public void usePoint(PointCommand command) {
        pointRepository.use(command.userId(), command.point());
    }
}
