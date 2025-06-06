package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.infrastructure.repository.SeatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeatTest {

    @Test
    @DisplayName("정상적인 값으로 Seat 생성")
    void createSeat_success() {
        Seat seat = Seat.builder()
                .venueId(1L)
                .seatNumber(10)
                .build();

        assertThat(seat.getSeatNumber()).isEqualTo(10);
    }

    @Test
    @DisplayName("seatNumber가 0 이하이면 예외 발생")
    void createSeat_seatNumber_zeroOrNegative() {
        assertThatThrownBy(() -> Seat.builder()
                .venueId(1L)
                .seatNumber(0)
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("seatNumber must be greater than 0");
    }

    @Test
    @DisplayName("seatNumber가 50 초과이면 예외 발생")
    void createSeat_seatNumber_tooHigh() {
        assertThatThrownBy(() -> Seat.builder()
                .venueId(1L)
                .seatNumber(51)
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("seatNumber must be less than 50");
    }

    @Test
    @DisplayName("chooseSeat는 SeatRepository의 choose를 호출한다")
    void chooseSeat_success() {
        // given
        Seat seat = Seat.builder()
                .venueId(1L)
                .seatNumber(5)
                .build();

        SeatRepository mockRepository = mock(SeatRepository.class);

        // when
//        seat.chooseSeat(mockRepository);

        // then
        verify(mockRepository, times(1)).choose(1);
    }
}