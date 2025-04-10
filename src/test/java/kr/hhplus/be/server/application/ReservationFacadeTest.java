package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.dto.ReservationCriteria;
import kr.hhplus.be.server.domain.reservation.ReservationItem;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatCommand;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationFacadeUnitTest {

    private ReservationService reservationService;
    private SeatService seatService;
    private ReservationFacade reservationFacade;

    @BeforeEach
    void setUp() {
        reservationService = mock(ReservationService.class);
        seatService = mock(SeatService.class);
        reservationFacade = new ReservationFacade(reservationService, seatService);
    }

    @Test
    @DisplayName("reserveSeats()는 좌석 예약 후 ReservationCommand를 만들어 예약 서비스에 전달한다")
    void reserveSeats_success() {
        // given
        UUID userId = UUID.randomUUID();
        List<Integer> seatNumbers = List.of(1, 2);

        ReservationCriteria criteria = new ReservationCriteria(userId,1L, seatNumbers);

        List<Seat> mockSeats = List.of(
                new Seat(1L, 100L, 10, SeatStatus.EMPTY),
                new Seat(2L, 100L, 11, SeatStatus.EMPTY)
        );

        when(seatService.reserveSeat(any(SeatCommand.class))).thenReturn(mockSeats);

        // when
        reservationFacade.reserveSeats(criteria);

        // then
        verify(seatService).reserveSeat(any(SeatCommand.class));
        verify(reservationService).reserve(argThat(command -> {
            return command.userId().equals(userId)
                    && command.status() == ReservationStatus.WAITING
                    && command.items().size() == 2
                    && command.items().get(0).seatId() == 1L;
        }));
    }

    @Test
    @DisplayName("cancelReservation()은 DEAD 상태의 예약 아이템을 찾아 좌석을 해제한다")
    void cancelReservation_shouldUnreserveSeats() {
        // given
        List<ReservationItem> deadItems = List.of(
                new ReservationItem(999L, 1L),
                new ReservationItem(999L, 2L)
        );

        when(reservationService.getDeadItems(any())).thenReturn(deadItems);

        // when
        reservationFacade.cancelReservation();

        // then
        verify(reservationService).getDeadItems(any());
        verify(seatService, times(2)).unReserveSeat(argThat(command ->
                command.seatId() == 1L || command.seatId() == 2L
        ));
    }
}