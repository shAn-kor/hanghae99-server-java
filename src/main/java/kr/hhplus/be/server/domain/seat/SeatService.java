package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.infrastructure.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;

    public List<Seat> getEmptySeats() {
        return seatRepository.getEmptySeats();
    }

    public List<Seat> reserveSeat(SeatCommand command) {
        List<Seat> seats = new LinkedList<>();

        for (Integer seatNumber : command.seatNumbers()) {
            Seat seat = seatRepository.getSeat(command.concertScheduleId(), seatNumber);
            seat.reserve();
            seatRepository.save(seat);
            seats.add(seat);
        }
        return seats;
    }

    public void unReserveSeat(SeatIdCommand command) {
        Seat seat = seatRepository.findById(command.seatId());
        seat.unReserve();
        seatRepository.save(seat);
    }
}
