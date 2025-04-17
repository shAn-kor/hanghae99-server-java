package kr.hhplus.be.server.domain.pointhistory;

import kr.hhplus.be.server.infrastructure.repository.PointHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static kr.hhplus.be.server.domain.pointhistory.PointHistoryType.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PointHistoryServiceUnitTest {

    private PointHistoryRepository pointHistoryRepository;
    private PointHistoryService pointHistoryService;

    @BeforeEach
    void setUp() {
        pointHistoryRepository = mock(PointHistoryRepository.class);
        pointHistoryService = new PointHistoryService(pointHistoryRepository);
    }

    @Test
    @DisplayName("getPointHistoryByPointId()는 포인트 ID에 해당하는 히스토리 목록을 반환한다")
    void getPointHistoryByPointId_success() {
        // given
        Long pointId = 1L;
        PointHistoryCommand command = PointHistoryCommand.builder().pointId(pointId).build();
        List<PointHistory> mockHistory = List.of(
                new PointHistory( pointId, CHARGE)
        );

        when(pointHistoryRepository.getPointHistory(pointId)).thenReturn(mockHistory);

        // when
        List<PointHistory> result = pointHistoryService.getPointHistoryByPointId(command);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPointId()).isEqualTo(pointId);
    }

    @Test
    @DisplayName("savePointHistory()는 포인트 히스토리를 저장한다")
    void savePointHistory_success() {
        // given
        Long pointId = 1L;
        PointHistoryCommand command = PointHistoryCommand.builder()
                .pointId(pointId)
                .type(CHARGE)
                .build();

        // when
        pointHistoryService.savePointHistory(command);

        // then
        verify(pointHistoryRepository, times(1)).saveHistory(any(PointHistory.class));
    }
}