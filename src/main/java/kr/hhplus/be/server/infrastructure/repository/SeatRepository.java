package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.seat.Seat;

import java.util.List;

public interface SeatRepository {
    Seat findById(Long id);

    Seat choose(Integer seatNumber);

    List<Seat> getEmptySeats();

    void save(Seat newSeat);

    Seat getSeat(Long aLong, Integer seatNumber);
}
