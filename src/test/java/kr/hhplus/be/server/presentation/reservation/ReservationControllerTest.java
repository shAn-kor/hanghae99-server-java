package kr.hhplus.be.server.presentation.reservation;

import kr.hhplus.be.server.application.ReservationFacade;
import kr.hhplus.be.server.presentation.reservation.object.ReservationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReservationControllerUnitTest {

    private ReservationFacade reservationFacade;
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        reservationFacade = mock(ReservationFacade.class);
        reservationController = new ReservationController(reservationFacade);
    }

    @Test
    @DisplayName("POST /reserve는 예약 요청을 처리하고 200 OK를 반환한다")
    void reserve_success() throws AccessDeniedException {
        // given
        UUID userId = UUID.randomUUID();
        ReservationRequest request = new ReservationRequest(userId, 1L, List.of(1, 2, 3));

        // when
        ResponseEntity.BodyBuilder response = reservationController.reserve(request);

        // then
        verify(reservationFacade).reserveSeats(ReservationRequest.toCriteria(request));
        assertThat(response.build().getStatusCode().is2xxSuccessful()).isTrue();
    }
}