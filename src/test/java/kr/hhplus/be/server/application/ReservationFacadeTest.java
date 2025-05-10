package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.concertschedule.ConcertScheduleService;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.token.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationFacadeUnitTest {
    private ConcertScheduleService concertScheduleService;
    private ReservationService reservationService;
    private SeatService seatService;
    private ReservationFacade reservationFacade;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        concertScheduleService = mock(ConcertScheduleService.class);
        reservationService = mock(ReservationService.class);
        seatService = mock(SeatService.class);
        tokenService = mock(TokenService.class);
        reservationFacade = new ReservationFacade(concertScheduleService, reservationService, seatService, tokenService);
    }

    @Test
    @DisplayName("reserveSeats()는 대기열을 통과한 유저만 예약할 수 있고 예약 서비스에 전달한다")
    void reserveSeats_success() throws AccessDeniedException {
        // given
        UUID userId = UUID.randomUUID();
        List<Long> seatNumbers = List.of(1L, 2L);
        ReservationCriteria criteria = ReservationCriteria.builder().uuid(userId).concertScheduleId(1L).seatList(seatNumbers).build();

        List<Seat> mockSeats = List.of(
                new Seat( 100L, 10),
                new Seat( 100L, 11)
        );

        when(seatService.reserveSeat(any())).thenReturn(mockSeats);

        // when
        reservationFacade.reserveSeats(criteria);

        // then
        verify(tokenService).isValid(argThat(cmd -> cmd.userId().equals(userId)));
        verify(seatService).reserveSeat(any());
        verify(reservationService).reserve(argThat(command ->
                command.userId().equals(userId)
                        && command.status() == ReservationStatus.WAITING
                        && command.items().size() == 2
                        && command.items().get(0).seatId() == 1L
        ));
    }
}