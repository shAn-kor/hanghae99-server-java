package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.Venue.Venue;

import java.util.List;

public interface VenueRepository {
    List<Venue> getConcertVenueList(Long venueId);
}
