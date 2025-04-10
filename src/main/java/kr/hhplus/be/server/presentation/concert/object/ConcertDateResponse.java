package kr.hhplus.be.server.presentation.concert.object;

import kr.hhplus.be.server.domain.concertdate.ConcertDate;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ConcertDateResponse(
        Long concertId,
        Long venueId,
        LocalDateTime concertDate
) {
    public static ConcertDateResponse from (ConcertDate concertDate) {
        return ConcertDateResponse.builder()
                .concertId(concertDate.concertId())
                .venueId(concertDate.venueId())
                .concertDate(concertDate.concertDate())
                .build();
    }

    public static List<ConcertDateResponse> from (List<ConcertDate> concertDates) {
        return concertDates.stream().map(ConcertDateResponse::from).toList();
    }
}
