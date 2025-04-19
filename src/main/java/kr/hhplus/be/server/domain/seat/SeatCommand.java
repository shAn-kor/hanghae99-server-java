package kr.hhplus.be.server.domain.seat;

import lombok.Builder;

import java.util.List;

@Builder
public record SeatCommand(
        Long concertScheduleId,
        List<Integer> seatNumbers
) {

    public SeatCommand (Long concertScheduleId, List<Integer> seatNumbers) {
        this.concertScheduleId = concertScheduleId;
        this.seatNumbers = seatNumbers;
    }
//    public SeatCommand {
//        if (concertScheduleId <= 0) {
//            throw new IllegalArgumentException("concertScheduleId must be greater than 0");
//        }
//
//        if (seatNumbers == null || seatNumbers.isEmpty()) {
//            throw new IllegalArgumentException("seatNumbers is null or empty");
//        }
//        if (seatNumbers.size() > 4) {
//            throw new IllegalArgumentException("you can only have 4 seats");
//        }
//    }
}


