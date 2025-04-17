package kr.hhplus.be.server.presentation.seat;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import kr.hhplus.be.server.presentation.seat.object.SeatRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SeatControllerUnitTest {

    private SeatService seatService;
    private SeatController seatController;

    @BeforeEach
    void setUp() {
        seatService = mock(SeatService.class);
        seatController = new SeatController(seatService);
    }

    @Test
    @DisplayName("emptySeatList()는 비어있는 좌석 번호 목록을 반환한다")
    void emptySeatList_success() {
        // given
        SeatRequest request = new SeatRequest(LocalDateTime.now(), UUID.randomUUID());
        List<Seat> emptySeats = List.of(
                new Seat( 1L, 10, SeatStatus.EMPTY),
                new Seat( 1L, 20, SeatStatus.EMPTY)
        );
        when(seatService.getEmptySeats()).thenReturn(emptySeats);

        // when
        ResponseEntity<List<Integer>> response = seatController.emptySeatList(request);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsExactly(10, 20);
    }
}