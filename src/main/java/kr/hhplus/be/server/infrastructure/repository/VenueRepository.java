package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.venue.Venue;

public interface VenueRepository {
    Venue getConcertVenue(Long venueId);

    Venue save(Venue savedVenue);
}
