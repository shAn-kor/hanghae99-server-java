package kr.hhplus.be.server.domain.reservation;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ReservationCommand(
        UUID userId,
        ReservationStatus status,
        LocalDateTime createdAt,
        List<ReservationItemCommand> items
) {
    public Reservation toReservation() {
        return Reservation.builder()
                .userId(userId)
                .status(ReservationStatus.WAITING)
                .build();
    }
}
