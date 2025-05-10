package kr.hhplus.be.server.domain.venue;

import kr.hhplus.be.server.domain.concert.ConcertCommand;
import kr.hhplus.be.server.infrastructure.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;

    public Venue getConcertVenue(ConcertCommand command) {
        return venueRepository.getConcertVenue(command.venueId());
    }
}
