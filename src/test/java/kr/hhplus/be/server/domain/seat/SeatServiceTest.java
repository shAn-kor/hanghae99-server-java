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
    @DisplayName("getEmptySeats()는 seatRepository에서 비어 있는 좌석을 조회한다")
    void getEmptySeats_returnsEmptySeats() {
        // given
        List<Seat> mockSeats = List.of(
                new Seat(1L, 101L, 10, SeatStatus.EMPTY),
                new Seat(2L, 101L, 11, SeatStatus.EMPTY)
        );
        when(seatRepository.getEmptySeats()).thenReturn(mockSeats);

        // when
        List<Seat> result = seatService.getEmptySeats();

        // then
        assertThat(result).hasSize(2);
        verify(seatRepository).getEmptySeats();
    }

    @Test
    @DisplayName("reserveSeat()는 seatNumbers로 좌석을 선택한다")
    void reserveSeat_reservesGivenSeats() {
        // given
        List<Integer> seatNumbers = List.of(10, 11);
        SeatCommand command = new SeatCommand(1L, seatNumbers);

        when(seatRepository.choose(10)).thenReturn(new Seat(1L, 101L, 10, SeatStatus.RESERVED));
        when(seatRepository.choose(11)).thenReturn(new Seat(2L, 101L, 11, SeatStatus.RESERVED));

        // when
        List<Seat> result = seatService.reserveSeat(command);

        // then
        assertThat(result).hasSize(2);
        verify(seatRepository).choose(10);
        verify(seatRepository).choose(11);
    }

    @Test
    @DisplayName("unReserveSeat()는 좌석 ID로 해제 요청을 한다")
    void unReserveSeat_shouldCallRepository() {
        // given
        SeatIdCommand command = new SeatIdCommand(99L);

        // when
        seatService.unReserveSeat(command);

        // then
        verify(seatRepository).unReserveSeat(99L);
    }
}