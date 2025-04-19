package kr.hhplus.be.server.domain.concertschedule;

import kr.hhplus.be.server.domain.concert.ConcertCommand;
import kr.hhplus.be.server.infrastructure.repository.ConcertScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertScheduleService {
    private final ConcertScheduleRepository concertScheduleRepository;

    public List<ConcertSchedule> getConcertDates(ConcertCommand command) {
        return concertScheduleRepository.getConcertDates(command.concertId());
    }
}
