package kr.hhplus.be.server.domain.concertschedule;

import kr.hhplus.be.server.domain.concert.ConcertCommand;
import kr.hhplus.be.server.infrastructure.repository.ConcertScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertScheduleService {
    private final ConcertScheduleRepository concertScheduleRepository;

    @Cacheable(value = "concertDates", key = "#command.concertId()", cacheManager = "cacheManager")
    public List<ConcertSchedule> getConcertDates(ConcertCommand command) {
        return concertScheduleRepository.getConcertDates(command.concertId());
    }

    public ConcertSchedule getConcertSchedule(ConcertScheduleCommand concertScheduleCommand) {
        return concertScheduleRepository.getConcertSchedule(concertScheduleCommand.concertScheduleId());
    }

    public Integer getTotalTicketCount(ConcertScheduleCommand command) {
        List<ConcertSchedule> list = concertScheduleRepository.getConcertDates(command.concertId());
        return list.size() * 50;
    }

    public List<Long> getConcertScheduleIdList(ConcertScheduleCommand command) {
        return concertScheduleRepository.getConcertDates(command.concertId()).stream().map(ConcertSchedule::getConcertScheduleId).toList();
    }
}
