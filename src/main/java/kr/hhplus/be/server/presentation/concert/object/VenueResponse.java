package kr.hhplus.be.server.presentation.concert.object;

import kr.hhplus.be.server.domain.venue.Venue;
import lombok.Builder;

import java.util.List;

@Builder
public record VenueResponse(
        String venueName,
        String address
) {
    public static VenueResponse from(Venue venue) {
        return VenueResponse.builder().venueName(venue.getVenueName()).address(venue.getAddress()).build();
    }

    public static List<VenueResponse> from(List<Venue> venues) {
        return venues.stream().map(VenueResponse::from).toList();
    }
}
