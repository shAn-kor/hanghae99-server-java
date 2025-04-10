package kr.hhplus.be.server.domain.Venue;

import lombok.Builder;

@Builder
public record Venue(
        Long venueId,
        String venueName,
        String address,
        Integer seatCount
) {
    public Venue {
        if (venueId == null) {
            throw new IllegalArgumentException("venueId is null");
        }
        if (venueId <= 0) {
            throw new IllegalArgumentException("venueId must be greater than 0");
        }

        if (seatCount == null) {
            throw new IllegalArgumentException("seatCount is null");
        }
        if (seatCount <= 0) {
            throw new IllegalArgumentException("seatCount must be greater than 0");
        }
        if (seatCount > 50) {
            throw new IllegalArgumentException("seatCount must be less than 50");
        }
    }
}
