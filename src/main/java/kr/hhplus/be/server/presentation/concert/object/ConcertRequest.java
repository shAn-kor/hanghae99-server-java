package kr.hhplus.be.server.presentation.concert.object;

import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.domain.concert.ConcertCommand;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ConcertRequest(
        @Positive Long concertId,
        @Positive Long venueId,
        @Positive LocalDateTime date
) {
    public ConcertCommand toCommand() {
        return ConcertCommand.builder().concertId(concertId).venueId(venueId).build();
    }
}
