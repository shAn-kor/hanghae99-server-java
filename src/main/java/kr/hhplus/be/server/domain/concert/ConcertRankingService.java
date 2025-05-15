package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.infrastructure.repository.ConcertRankingRepository;
import kr.hhplus.be.server.infrastructure.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertRankingService {
    private final ConcertRankingRepository repository;
    private final ConcertRepository concertRepository;

    public void registerSoldOutConcert(ConcertCommand command) {
        LocalDateTime openTime = concertRepository.getConcert(command.concertId()).get().getTicketTime();
        long secondsSinceOpen = Duration.between(openTime, LocalDateTime.now()).getSeconds();

        repository.save(command.concertId(), secondsSinceOpen);
    }

    public List<ConcertRankResult> rankConcert() {
        return repository.getRanks();
    }
}
