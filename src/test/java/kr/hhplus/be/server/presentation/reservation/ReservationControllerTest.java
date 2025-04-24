package kr.hhplus.be.server.presentation.reservation;

import kr.hhplus.be.server.application.ReservationFacade;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ReservationControllerUnitTest {

    private ReservationFacade reservationFacade;
    private ReservationService reservationService;
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        reservationFacade = mock(ReservationFacade.class);
        reservationService = mock(ReservationService.class);
        reservationController = new ReservationController(reservationFacade, reservationService);
    }

    @Test
    @DisplayName("POST /reserve는 예약 요청을 처리하고 200 OK를 반환한다")
    void reserve_success() throws AccessDeniedException {
        // given
        UUID userId = UUID.randomUUID();
        ReservationRequest request = new ReservationRequest(userId, 1L, List.of(1L, 2L, 3L));

        // when
        ResponseEntity.BodyBuilder response = reservationController.reserve(request);

        // then
        verify(reservationFacade).reserveSeats(ReservationRequest.toCriteria(request));
        assertThat(response.build().getStatusCode().is2xxSuccessful()).isTrue();
    }
}