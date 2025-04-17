package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.infrastructure.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PointServiceTest {

    private PointRepository pointRepository;
    private PointService pointService;

    @BeforeEach
    void setUp() {
        pointRepository = mock(PointRepository.class);
        pointService = new PointService(pointRepository);
    }

    @Test
    @DisplayName("getPointByUserId()는 repository에서 Point를 반환한다")
    void getPointByUserId_success() {
        UUID userId = UUID.randomUUID();
        PointCommand command = new PointCommand(userId, 100L);
        Point mockPoint = new Point( userId, 1000L);

        when(pointRepository.getPoint(userId)).thenReturn(mockPoint);

        Point result = pointService.getPointByUserId(command);

        assertThat(result).isEqualTo(mockPoint);
        verify(pointRepository).getPoint(userId);
    }

    @Test
    @DisplayName("checkPoint()는 Point 객체의 checkPoint를 호출한다")
    void checkPoint_success() {
        UUID userId = UUID.randomUUID();
        PointCommand command = new PointCommand(userId, 0L);

        Point mockPoint = mock(Point.class);
        when(pointRepository.getPoint(userId)).thenReturn(mockPoint);
        when(mockPoint.checkPoint(command.point())).thenReturn(true);

        pointService.checkPoint(command);

        verify(mockPoint).checkPoint(0L);
    }

    @Test
    @DisplayName("chargePoint()는 Point 객체의 charge() 호출")
    void chargePoint_success() {
        UUID userId = UUID.randomUUID();
        PointCommand command = new PointCommand(userId, 500L);

        Point mockPoint = mock(Point.class);
        when(pointRepository.getPoint(userId)).thenReturn(mockPoint);

        pointService.chargePoint(command);

        verify(mockPoint).charge(500L);
    }

    @Test
    @DisplayName("usePoint()는 Point 객체의 use() 호출")
    void usePoint_success() {
        UUID userId = UUID.randomUUID();
        PointCommand command = new PointCommand(userId, 300L);

        Point mockPoint = mock(Point.class);
        when(pointRepository.getPoint(userId)).thenReturn(mockPoint);

        pointService.usePoint(command);

        verify(mockPoint).use(300L);
    }
}