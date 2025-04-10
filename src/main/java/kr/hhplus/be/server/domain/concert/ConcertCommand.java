package kr.hhplus.be.server.domain.concert;

import lombok.Builder;

@Builder
public record ConcertCommand(
        Long concertId,
        Long venueId
) {
}
