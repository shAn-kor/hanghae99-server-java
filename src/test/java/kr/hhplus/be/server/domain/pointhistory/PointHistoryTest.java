package kr.hhplus.be.server.domain.pointhistory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointHistoryTest {

    @Test
    @DisplayName("정상적으로 PointHistory 생성")
    void createPointHistory_success() {
        PointHistory history = new PointHistory(
                1L,
                10L,
                PointHistoryType.CHARGE,
                LocalDateTime.now()
        );

        assertThat(history.historyId()).isEqualTo(1L);
        assertThat(history.pointId()).isEqualTo(10L);
        assertThat(history.type()).isEqualTo(PointHistoryType.CHARGE);
    }

    @Test
    @DisplayName("historyId가 null이면 예외 발생")
    void createPointHistory_historyId_null() {
        assertThatThrownBy(() -> new PointHistory(
                null,
                10L,
                PointHistoryType.CHARGE,
                LocalDateTime.now()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("historyId must not be null");
    }

    @Test
    @DisplayName("historyId가 0 이하이면 예외 발생")
    void createPointHistory_historyId_invalid() {
        assertThatThrownBy(() -> new PointHistory(
                0L,
                10L,
                PointHistoryType.CHARGE,
                LocalDateTime.now()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("historyId must be greater than 0");
    }

    @Test
    @DisplayName("pointId가 null이면 예외 발생")
    void createPointHistory_pointId_null() {
        assertThatThrownBy(() -> new PointHistory(
                1L,
                null,
                PointHistoryType.CHARGE,
                LocalDateTime.now()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pointId must not be null");
    }

    @Test
    @DisplayName("pointId가 0 이하이면 예외 발생")
    void createPointHistory_pointId_invalid() {
        assertThatThrownBy(() -> new PointHistory(
                1L,
                0L,
                PointHistoryType.CHARGE,
                LocalDateTime.now()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pointId must be greater than 0");
    }
}