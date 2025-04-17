package kr.hhplus.be.server.infrastructure.repository.impl;

import kr.hhplus.be.server.domain.Venue.Venue;
import kr.hhplus.be.server.infrastructure.jpa.JpaVenueRepository;
import kr.hhplus.be.server.infrastructure.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VenueRepositoryImpl implements VenueRepository {
    private final JpaVenueRepository jpaVenueRepository;

    @Override
    public Venue getConcertVenue(Long venueId) {
        return jpaVenueRepository.getOne(venueId);
    }

    @Override
    public Venue save(Venue savedVenue) {
        jpaVenueRepository.save(savedVenue);
        return savedVenue;
    }
}
