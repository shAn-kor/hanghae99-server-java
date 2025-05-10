package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.concertschedule.ConcertSchedule;
import kr.hhplus.be.server.domain.concertschedule.ConcertScheduleCommand;
import kr.hhplus.be.server.domain.concertschedule.ConcertScheduleService;
import kr.hhplus.be.server.domain.reservation.*;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatCommand;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.token.TokenCommand;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.infrastructure.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationFacade {
    private final ConcertScheduleService concertScheduleService;
    private final ReservationService reservationService;
    private final SeatService seatService;
    private final TokenService tokenService;

    @DistributedLock(prefix = "reservation:reserve", key = "#criteria.uuid()")
    public void reserveSeats(ReservationCriteria criteria) throws AccessDeniedException {
        ConcertSchedule schedule = concertScheduleService.getConcertSchedule(ConcertScheduleCommand.builder().concertScheduleId(criteria.concertScheduleId()).build());

        tokenService.isValid(TokenCommand.builder().userId(criteria.uuid()).concertId(schedule.getConcertId()).build());

        List<Seat> seatList = seatService.reserveSeat(ReservationCriteria.toSeatCommand(criteria));

        List<ReservationItemCommand> reservationItems = seatList.stream()
                .map(seat ->
                        ReservationItemCommand.builder()
                                .seatId(
                                        seat.getSeatId()
                                ).build())
                .toList();

        ReservationCommand command = ReservationCommand.builder()
                        .userId(criteria.uuid())
                .concertScheduleId(criteria.concertScheduleId())
                                .status(ReservationStatus.WAITING)
                                        .items(reservationItems)
                .build();
        reservationService.reserve(command);
    }

    @Transactional(readOnly = true)
    public ReservationItemResult getEmptySeat(ReservationCriteria criteria) {
        ReservationCommand command = ReservationCriteria.toReservationCommand(criteria);
        List<ReservationItem> getReservedItems = reservationService.getReservedItems(command);
        List<Long> reservedSeatIdList = getReservedItems.stream().map(reservationItem -> reservationItem.getId().getSeatId()).toList();
        ConcertSchedule schedule = concertScheduleService.getConcertSchedule(criteria.toConcertScheduleCommand());
        SeatCommand seatCommand = SeatCommand.builder().venueId(schedule.getVenueId()).seatNumbers(reservedSeatIdList).build();
        List<Seat> getEmptySeats = seatService.getEmptySeats(seatCommand);
        List<Long> emptySeatIdList = getEmptySeats.stream().map(Seat::getSeatId).toList();
        return ReservationItemResult.builder().seatIdList(emptySeatIdList).build();
    }
}
