package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.infrastructure.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;

    public List<Seat> getEmptySeats(SeatCommand seatCommand) {
        return seatRepository.getEmptySeats(seatCommand.venueId(), seatCommand.seatNumbers());
    }

    public List<Seat> reserveSeat(SeatCommand command) {
        return seatRepository.findWithPessimisticLock(command.venueId(), command.seatNumbers());
    }
}
