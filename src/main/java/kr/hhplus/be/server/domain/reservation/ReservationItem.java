package kr.hhplus.be.server.domain.reservation;

import lombok.Builder;

@Builder
public record ReservationItem(
        Long reservationId,
        Long seatId
) {
    public ReservationItem {
        if (seatId == null) {
            throw new IllegalArgumentException("seatId must not be null");
        }
        if (reservationId == null) {
            throw new IllegalArgumentException("reservationId must not be null");
        }
        if (seatId <= 0) {
            throw new IllegalArgumentException("seatId must be greater than zero");
        }
        if (reservationId <= 0) {
            throw new IllegalArgumentException("reservationId must be greater than zero");
        }
    }
}
