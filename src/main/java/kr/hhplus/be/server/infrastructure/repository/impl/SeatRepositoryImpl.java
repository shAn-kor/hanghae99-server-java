package kr.hhplus.be.server.infrastructure.repository.impl;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.infrastructure.jpa.JpaSeatRepository;
import kr.hhplus.be.server.infrastructure.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {
    private final JpaSeatRepository seatRepository;

    @Override
    public Seat findById(Long id) {
        return seatRepository.findById(id).orElse(null);
    }

    @Override
    public Seat choose(Integer seatNumber) {
        return seatRepository.findBySeatNumber(seatNumber);
    }

    @Override
    public List<Seat> getEmptySeats(Long venueId, List<Long> reservedSeatIds) {
        return seatRepository.getUnreservedSeats(venueId, reservedSeatIds);
    }

    @Override
    public void save(Seat newSeat) {
        seatRepository.save(newSeat);
    }

    @Override
    public Seat getSeat(Long concertScheduleId, Integer seatNumber) {
        return seatRepository.findByConcertScheduleIdAndSeatNumber(concertScheduleId, seatNumber);
    }
}
