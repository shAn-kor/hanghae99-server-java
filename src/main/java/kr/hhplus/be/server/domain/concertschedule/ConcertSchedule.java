package kr.hhplus.be.server.domain.concertschedule;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ConcertSchedule(
        Long concertId,
        Long venueId,
        LocalDateTime concertDate
) {
    public ConcertSchedule {
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
