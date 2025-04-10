package kr.hhplus.be.server.domain.concertdate;

import kr.hhplus.be.server.domain.concert.ConcertCommand;
import kr.hhplus.be.server.infrastructure.repository.ConcertDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertDateService {
    private final ConcertDateRepository concertDateRepository;

    public List<ConcertDate> getConcertDates(ConcertCommand command) {
        return concertDateRepository.getConcertDates(command.concertId());
    }
}
