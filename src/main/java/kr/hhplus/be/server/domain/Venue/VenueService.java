package kr.hhplus.be.server.domain.Venue;

import kr.hhplus.be.server.domain.concert.ConcertCommand;
import kr.hhplus.be.server.infrastructure.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;

    public List<Venue> getConcertVenueList(ConcertCommand command) {
        return venueRepository.getConcertVenueList(command.venueId());
    }
}
