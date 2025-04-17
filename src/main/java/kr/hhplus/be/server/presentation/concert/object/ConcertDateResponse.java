package kr.hhplus.be.server.presentation.concert.object;

import kr.hhplus.be.server.domain.concertschedule.ConcertSchedule;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ConcertDateResponse(
        Long concertId,
        Long venueId,
        LocalDateTime concertDate
) {
    public static ConcertDateResponse from (ConcertSchedule concertSchedule) {
        return ConcertDateResponse.builder()
                .concertId(concertSchedule.getConcert().getConcertId())
                .venueId(concertSchedule.getVenue().getVenueId())
                .concertDate(concertSchedule.getConcertDate())
                .build();
    }

    public static List<ConcertDateResponse> from (List<ConcertSchedule> concertSchedules) {
        return concertSchedules.stream().map(ConcertDateResponse::from).toList();
    }
}
