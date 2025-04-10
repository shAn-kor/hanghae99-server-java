package kr.hhplus.be.server.domain.seat;

import lombok.Builder;

import java.util.List;

@Builder
public record SeatCommand(
        Long concertDateId,
        List<Integer> seatNumbers
) {
    public SeatCommand {
        if (concertDateId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        if (concertDateId <= 0) {
            throw new IllegalArgumentException("concertDateId must be greater than 0");
        }

        if (seatNumbers == null || seatNumbers.isEmpty()) {
            throw new IllegalArgumentException("seatNumbers is null or empty");
        }
        if (seatNumbers.size() > 4) {
            throw new IllegalArgumentException("you can only have 4 seats");
        }
    }
}
