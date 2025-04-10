package kr.hhplus.be.server.domain.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class PointTest {

    @Test
    @DisplayName("정상적으로 Point 객체 생성된다")
    void createPoint_success() {
        UUID userId = UUID.randomUUID();
        Point point = new Point(1L, userId, 5000L);

        assertThat(point.pointId()).isEqualTo(1L);
        assertThat(point.userId()).isEqualTo(userId);
        assertThat(point.balance()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("pointId가 0이면 예외 발생")
    void createPoint_invalid_pointId() {
        UUID userId = UUID.randomUUID();
        assertThatThrownBy(() -> new Point(0L, userId, 5000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pointId must be greater than 0");
    }

    @Test
    @DisplayName("userId가 null이면 예외 발생")
    void createPoint_null_userId() {
        assertThatThrownBy(() -> new Point(1L, null, 5000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userId must be greater than 0");
    }

    @Test
    @DisplayName("balance가 null이면 예외 발생")
    void createPoint_null_balance() {
        UUID userId = UUID.randomUUID();
        assertThatThrownBy(() -> new Point(1L, userId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("balance must be greater than 0");
    }

    @Test
    @DisplayName("balance가 0 이하면 예외 발생")
    void createPoint_zeroOrNegative_balance() {
        UUID userId = UUID.randomUUID();
        assertThatThrownBy(() -> new Point(1L, userId, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("balance must be greater than 0");
    }

    @Test
    @DisplayName("checkPoint()는 잔액이 충분하면 true 반환")
    void checkPoint_true() {
        Point point = new Point(1L, UUID.randomUUID(), 10000L);
        assertThat(point.checkPoint(5000L)).isTrue();
    }

    @Test
    @DisplayName("checkPoint()는 잔액이 부족하면 false 반환")
    void checkPoint_false() {
        Point point = new Point(1L, UUID.randomUUID(), 1000L);
        assertThat(point.checkPoint(5000L)).isFalse();
    }
}