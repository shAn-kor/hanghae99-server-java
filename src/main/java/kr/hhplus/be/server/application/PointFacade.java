package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.dto.PointCriteria;
import kr.hhplus.be.server.application.dto.PointHistoryResult;
import kr.hhplus.be.server.application.dto.PointResult;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.pointhistory.PointHistory;
import kr.hhplus.be.server.domain.pointhistory.PointHistoryCommand;
import kr.hhplus.be.server.domain.pointhistory.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static kr.hhplus.be.server.domain.pointhistory.PointHistoryType.*;

@Service
@RequiredArgsConstructor
public class PointFacade {
    private final PointService pointService;
    private final PointHistoryService pointHistoryService;

    public PointResult getPoint(PointCriteria criteria) {
        Point point = pointService.getPointByUserId(criteria.toPointCommand());

        PointHistoryCommand historyCommand = PointHistoryCommand.builder()
                .pointId(point.getPointId())
                .type(CHECK)
                .build();
        pointHistoryService.savePointHistory(historyCommand);

        return PointResult.builder().uuid(point.getUserId()).point(point.getBalance()).build();
    }

    public void chargePoint(PointCriteria criteria) {
        pointService.chargePoint(criteria.toPointCommand());
        Point point = pointService.getPointByUserId(criteria.toPointCommand());
        PointHistoryCommand command = PointHistoryCommand.builder()
                .pointId(point.getPointId())
                .type(CHARGE)
                .build();
        pointHistoryService.savePointHistory(command);
    }

    public void usePoint(PointCriteria criteria) {
        pointService.usePoint(criteria.toPointCommand());
        Point point = pointService.getPointByUserId(criteria.toPointCommand());
        PointHistoryCommand command = PointHistoryCommand.builder()
                .pointId(point.getPointId())
                .type(USE)
                .build();
        pointHistoryService.savePointHistory(command);
    }

    public List<PointHistoryResult> getPointHistory(PointCriteria pointCriteria) {
        Point point = pointService.getPointByUserId(pointCriteria.toPointCommand());
        List<PointHistory> historyList = pointHistoryService.getPointHistoryByPointId(
                PointHistoryCommand.builder()
                        .pointId(point.getPointId()).build()
        );
        return historyList.stream()
                .map(ph -> new PointHistoryResult(
                        ph.getPointId(),
                        ph.getType(),
                        ph.getCreatedAt()
                )).toList();
    }
}
