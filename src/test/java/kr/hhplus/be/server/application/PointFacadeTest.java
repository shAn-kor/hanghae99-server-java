package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.pointhistory.PointHistory;
import kr.hhplus.be.server.domain.pointhistory.PointHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static kr.hhplus.be.server.domain.pointhistory.PointHistoryType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PointFacadeUnitTest {

    private PointService pointService;
    private PointHistoryService pointHistoryService;
    private PointFacade pointFacade;

    @BeforeEach
    void setUp() {
        pointService = mock(PointService.class);
        pointHistoryService = mock(PointHistoryService.class);
        pointFacade = new PointFacade(pointService, pointHistoryService);
    }

    @Test
    @DisplayName("getPoint()는 포인트 정보와 조회 히스토리를 저장한다")
    void getPoint_success() {
        UUID userId = UUID.randomUUID();
        Long pointId = 1L;
        Point point = new Point( userId, 1000L);
        PointCriteria criteria = new PointCriteria(userId, 1000L);

        when(pointService.getPointByUserId(any(PointCommand.class))).thenReturn(point);

        PointResult result = pointFacade.getPoint(criteria);

        assertThat(result.uuid()).isEqualTo(userId);
        assertThat(result.point()).isEqualTo(1000L);

        verify(pointHistoryService).savePointHistory(argThat(cmd ->
                cmd.pointId().equals(pointId) && cmd.type() == CHECK));
    }

    @Test
    @DisplayName("chargePoint()는 포인트 충전 및 충전 히스토리를 저장한다")
    void chargePoint_success() {
        UUID userId = UUID.randomUUID();
        Long pointId = 1L;
        Point point = new Point( userId, 5000L);
        PointCriteria criteria = new PointCriteria(userId, 1000L);

        when(pointService.getPointByUserId(any(PointCommand.class))).thenReturn(point);

        pointFacade.chargePoint(criteria);

        verify(pointService).chargePoint(any(PointCommand.class));
        verify(pointService, times(1)).getPointByUserId(any(PointCommand.class));
        verify(pointHistoryService).savePointHistory(argThat(cmd ->
                cmd.pointId().equals(pointId) && cmd.type() == CHARGE));
    }

    @Test
    @DisplayName("usePoint()는 포인트 차감 및 사용 히스토리를 저장한다")
    void usePoint_success() {
        UUID userId = UUID.randomUUID();
        Long pointId = 1L;
        Point point = new Point( userId, 3000L);
        PointCriteria criteria = new PointCriteria(userId, 1000L);

        when(pointService.getPointByUserId(any(PointCommand.class))).thenReturn(point);

        pointFacade.usePoint(criteria);

        verify(pointService).usePoint(any(PointCommand.class));
        verify(pointService, times(1)).getPointByUserId(any(PointCommand.class));
        verify(pointHistoryService).savePointHistory(argThat(cmd ->
                cmd.pointId().equals(pointId) && cmd.type() == USE));
    }

    @Test
    @DisplayName("getPointHistory()는 포인트 히스토리 리스트를 반환한다")
    void getPointHistory_success() {
        UUID userId = UUID.randomUUID();
        Long pointId = 1L;
        PointCriteria criteria = new PointCriteria(userId, 1000L);
        Point point = new Point( userId, 1000L);
        PointHistory history = new PointHistory( pointId, USE);

        when(pointService.getPointByUserId(any())).thenReturn(point);
        when(pointHistoryService.getPointHistoryByPointId(any()))
                .thenReturn(List.of(history));

        List<PointHistoryResult> result = pointFacade.getPointHistory(criteria);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).pointId()).isEqualTo(pointId);
        assertThat(result.get(0).type()).isEqualTo(USE);
    }
}