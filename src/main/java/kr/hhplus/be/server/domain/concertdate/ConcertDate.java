package kr.hhplus.be.server.domain.concertdate;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ConcertDate(
        Long concertId,
        Long venueId,
        LocalDateTime concertDate
) {
    public ConcertDate {
        if (concertId == null) {
            throw new IllegalArgumentException("concertId must not be null");
        }
        if (concertId <= 0) {
            throw new IllegalArgumentException("concertId must be positive");
        }

        if (venueId != null && venueId <= 0) {
            throw new IllegalArgumentException("venueId must be positive");
        }

        if (concertDate != null && concertDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("concertDate must be before now");
        }
    }
}
