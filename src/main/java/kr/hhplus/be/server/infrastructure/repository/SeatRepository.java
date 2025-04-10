package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.seat.Seat;

import java.util.List;

public interface SeatRepository {
    Seat choose(Integer seatNumber);

    List<Seat> getEmptySeats();

    void unReserveSeat(Long seatId);
}
