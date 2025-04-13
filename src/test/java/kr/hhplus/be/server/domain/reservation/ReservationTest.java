package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationTest {

    @Test
    @DisplayName("정상적으로 Reservation 생성")
    void createReservation_success() {
        UUID userId = UUID.randomUUID();
        Reservation reservation = new Reservation(
                1L,
                userId,
                ReservationStatus.WAITING,
                LocalDateTime.now(),
                new ArrayList<ReservationItem>()
        );

        assertThat(reservation.userId()).isEqualTo(userId);
        assertThat(reservation.status()).isEqualTo(ReservationStatus.WAITING);
    }

    @Test
    @DisplayName("userId가 null이면 생성 시 예외 발생")
    void createReservation_userId_null() {
        assertThatThrownBy(() -> new Reservation(
                1L,
                null,
                ReservationStatus.WAITING,
                LocalDateTime.now(),
                new ArrayList<ReservationItem>()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userId is null");
    }

    @Test
    @DisplayName("reserve()는 repository.save(this)를 호출한다")
    void reserve_success() {
        UUID userId = UUID.randomUUID();
        Reservation reservation = new Reservation(
                1L,
                userId,
                ReservationStatus.WAITING,
                LocalDateTime.now(),
                new ArrayList<ReservationItem>()
        );

        ReservationRepository mockRepo = mock(ReservationRepository.class);

        verify(mockRepo, times(1)).save(reservation);
    }
}