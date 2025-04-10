package kr.hhplus.be.server.domain.reservation;

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

    @BeforeEach
    void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        reservationService = new ReservationService(reservationRepository);
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
        when(mockReservation.reservationId()).thenReturn(999L);
        when(reservationRepository.save(any())).thenReturn(mockReservation);

        reservationService.reserve(command);

        verify(reservationRepository).save(any(Reservation.class));
        verify(reservationRepository, times(2)).saveItem(any(ReservationItem.class));
        verify(reservationRepository).saveItem(argThat(item -> item.seatId().equals(101L)));
        verify(reservationRepository).saveItem(argThat(item -> item.seatId().equals(102L)));
    }

    @Test
    @DisplayName("getDeadItems()는 3분 초과된 예약들의 예약 아이템을 모두 반환한다")
    void getDeadItems_success() {
        DeadlineItemCriteria criteria = new DeadlineItemCriteria(
                java.time.LocalDateTime.now().minusMinutes(3)
        );

        Reservation reservation1 = mock(Reservation.class);
        Reservation reservation2 = mock(Reservation.class);

        List<ReservationItem> items1 = List.of(
                new ReservationItem(1L, 101L),
                new ReservationItem(1L, 102L)
        );
        List<ReservationItem> items2 = List.of(
                new ReservationItem(2L, 201L)
        );

        when(reservation1.items()).thenReturn(items1);
        when(reservation2.items()).thenReturn(items2);
        when(reservationRepository.getDeadReservations(any())).thenReturn(List.of(reservation1, reservation2));

        List<ReservationItem> result = reservationService.getDeadItems(criteria);

        verify(reservationRepository).getDeadReservations(criteria.deadline());
        assert result.size() == 3;
        assert result.containsAll(items1);
        assert result.containsAll(items2);
    }
}