package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.infrastructure.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;

    public List<Seat> getEmptySeats() {
        return seatRepository.getEmptySeats();
    }

    public List<Seat> reserveSeat(SeatCommand command) {
        return command.seatNumbers().stream()
                .map(seatRepository::choose)
                .toList();
    }

    public void unReserveSeat(SeatIdCommand command) {
        seatRepository.unReserveSeat(command.seatId());
    }
}
