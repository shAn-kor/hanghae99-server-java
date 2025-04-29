package kr.hhplus.be.server.presentation.point;

import kr.hhplus.be.server.application.PointFacade;
import kr.hhplus.be.server.application.PointResult;
import kr.hhplus.be.server.application.PointCriteria;
import kr.hhplus.be.server.application.PointHistoryResult;
import kr.hhplus.be.server.domain.pointhistory.PointHistoryType;
import kr.hhplus.be.server.presentation.point.object.PointHistoryResponse;
import kr.hhplus.be.server.presentation.point.object.PointRequest;
import kr.hhplus.be.server.presentation.point.object.PointResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PointControllerUnitTest {

    private PointFacade pointFacade;
    private PointController pointController;

    @BeforeEach
    void setUp() {
        pointFacade = mock(PointFacade.class);
        pointController = new PointController(pointFacade);
    }

    @Test
    @DisplayName("getPoint()는 유저의 포인트 정보를 반환한다")
    void getPoint_success() {
        // given
        UUID userId = UUID.randomUUID();
        PointRequest request = new PointRequest(userId, null);
        PointResult result = new PointResult(userId, 1000L);

        when(pointFacade.getPoint(request.toCriteria())).thenReturn(result);

        // when
        ResponseEntity<PointResponse> response = pointController.getPoint(request);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().uuid()).isEqualTo(userId);
        assertThat(response.getBody().point()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("charge()는 포인트 충전 후 200 OK 반환")
    void charge_success() {
        // given
        UUID userId = UUID.randomUUID();
        PointRequest request = new PointRequest(userId, 500L);

        // when
        ResponseEntity.BodyBuilder response = pointController.charge(request);

        // then
        verify(pointFacade, times(1)).chargePoint(request.toCriteria());
        assertThat(response.build().getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("history()는 포인트 히스토리 리스트를 반환한다")
    void history_success() {
        // given
        UUID userId = UUID.randomUUID();
        PointRequest request = new PointRequest(userId, null);
        LocalDateTime now = LocalDateTime.now();

        List<PointHistoryResponse> mockHistory = List.of(
                new PointHistoryResponse(1L, PointHistoryType.USE, now)
        );
        List<PointHistoryResult> mockHistoryResult = List.of(
                new PointHistoryResult(1L, PointHistoryType.USE, now)
        );

        when(pointFacade.getPointHistory(PointCriteria.from(request)))
                .thenReturn(mockHistoryResult);

        // when
        ResponseEntity<List<PointHistoryResponse>> response = pointController.history(request);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).type()).isEqualTo(PointHistoryType.USE);
    }
}