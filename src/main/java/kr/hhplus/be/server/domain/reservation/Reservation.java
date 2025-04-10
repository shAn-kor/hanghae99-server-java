package kr.hhplus.be.server.domain.reservation;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record Reservation (
        Long reservationId,
        UUID userId,
        ReservationStatus status,
        LocalDateTime createdAt,
        List<ReservationItem> items
) {
    public Reservation {
        if (userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
    }
}
