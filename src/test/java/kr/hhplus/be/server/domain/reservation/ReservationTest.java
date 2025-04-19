package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ReservationTest {

    @Test
    @DisplayName("정상적으로 Reservation 생성")
    void createReservation_success() {
        UUID userId = UUID.randomUUID();
        Reservation reservation = new Reservation(
                userId,
                ReservationStatus.WAITING
        );

        assertThat(reservation.getUserId()).isEqualTo(userId);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.WAITING);
    }

    @Test
    @DisplayName("userId가 null이면 생성 시 예외 발생")
    void createReservation_userId_null() {
        assertThatThrownBy(() -> new Reservation(
                null,
                ReservationStatus.WAITING
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userId is null");
    }

    @Test
    @DisplayName("reserve()는 repository.save(this)를 호출한다")
    void reserve_success() {
        UUID userId = UUID.randomUUID();
        Reservation reservation = new Reservation(
                userId,
                ReservationStatus.WAITING
        );

        ReservationRepository mockRepo = mock(ReservationRepository.class);

        verify(mockRepo, times(1)).save(reservation);
    }
}