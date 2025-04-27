package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.infrastructure.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SeatServiceTest {

    private SeatRepository seatRepository;
    private SeatService seatService;

    @BeforeEach
    void setUp() {
        seatRepository = mock(SeatRepository.class);
        seatService = new SeatService(seatRepository);
    }

    @Test
    @DisplayName("reserveSeat()는 seatNumbers로 좌석을 선택한다")
    void reserveSeat_reservesGivenSeats() {
        // given
        List<Long> seatNumbers = List.of(10L, 11L);
        SeatCommand command = new SeatCommand(1L, seatNumbers);

        when(seatRepository.choose(10)).thenReturn(new Seat( 101L, 10));
        when(seatRepository.choose(11)).thenReturn(new Seat( 101L, 11));

        // when
        List<Seat> result = seatService.reserveSeat(command);

        // then
        assertThat(result).hasSize(2);
        verify(seatRepository).choose(10);
        verify(seatRepository).choose(11);
    }
}