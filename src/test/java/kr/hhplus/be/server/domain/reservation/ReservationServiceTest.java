package kr.hhplus.be.server.domain.reservation;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationRepository reservationRepository;
    private ReservationService reservationService;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        reservationService = new ReservationService(reservationRepository, entityManager);
    }

    @Test
    @DisplayName("reserve()는 Reservation과 ReservationItem을 저장한다")
    void reserve_success() {
        UUID userId = UUID.randomUUID();

        ReservationCommand command = ReservationCommand.builder()
                .userId(userId)
                .status(ReservationStatus.WAITING)
                .items(List.of(
                        ReservationItemCommand.builder().seatId(101L).build(),
                        ReservationItemCommand.builder().seatId(102L).build()
                ))
                .build();

        Reservation mockReservation = mock(Reservation.class);
        when(mockReservation.getReservationId()).thenReturn(999L);
        when(reservationRepository.save(any())).thenReturn(mockReservation);

        reservationService.reserve(command);

        verify(reservationRepository).save(any(Reservation.class));
        verify(reservationRepository, times(2)).saveItem(any(ReservationItem.class));
        verify(reservationRepository).saveItem(argThat(item -> item.getSeatId().equals(101L)));
        verify(reservationRepository).saveItem(argThat(item -> item.getSeatId().equals(102L)));
    }
}