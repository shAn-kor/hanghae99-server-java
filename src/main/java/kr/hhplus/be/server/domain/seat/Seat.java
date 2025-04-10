package kr.hhplus.be.server.domain.seat;

import lombok.Builder;

@Builder
public record Seat (
        Long seatId,
        Long concertDateId,
        Integer seatNumber,
        SeatStatus status
) {
    public Seat {
        if (seatId == null) {
            throw new IllegalArgumentException("seatId must not be null");
        }
        if (seatId <= 0) {
            throw new IllegalArgumentException("seatId must be greater than 0");
        }
        if (concertDateId == null) {
            throw new IllegalArgumentException("concertDateId must not be null");
        }
        if (seatNumber == null) {
            throw new IllegalArgumentException("seatNumber must not be null");
        }
        if (seatNumber <= 0) {
            throw new IllegalArgumentException("seatNumber must be greater than 0");
        }
        if (seatNumber > 50) {
            throw new IllegalArgumentException("seatNumber must be less than 50");
        }
    }
}
