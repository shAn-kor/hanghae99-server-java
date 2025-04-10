package kr.hhplus.be.server.presentation.concert.object;

import kr.hhplus.be.server.domain.concert.Concert;
import lombok.Builder;

import java.util.List;

@Builder
public record ConcertResponse(
        String concertName,
        String artist
) {
    public static ConcertResponse from (Concert concert) {
        return ConcertResponse.builder().concertName(concert.concertName()).artist(concert.artist()).build();
    }

    public static List<ConcertResponse> fromConcertList (List<Concert> list) {
        return list.stream().map(ConcertResponse::from).toList();
    }
}
