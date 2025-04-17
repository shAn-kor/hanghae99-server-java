package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.dto.ReservationCriteria;
import kr.hhplus.be.server.domain.reservation.*;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatIdCommand;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.token.TokenCommand;
import kr.hhplus.be.server.domain.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationFacade {
    private final ReservationService reservationService;
    private final SeatService seatService;
    private final TokenService tokenService;

    public void reserveSeats(ReservationCriteria criteria) throws AccessDeniedException {
        if (!tokenService.isValid(TokenCommand.builder().userId(criteria.uuid()).build())) {
            throw new AccessDeniedException("대기열을 통과하지 못했습니다.");
        }

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
                                .status(ReservationStatus.WAITING)
                                        .items(reservationItems)
                .build();
        reservationService.reserve(command);
    }

    @Scheduled(fixedRate = 60000)
    public void cancelReservation() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(3);
        List<ReservationItem> deadItems = reservationService.getDeadItems(new DeadlineItemCriteria(deadline));

        for (ReservationItem item : deadItems) {
            SeatIdCommand command = new SeatIdCommand(item.getSeatId());
            seatService.unReserveSeat(command);
        }
    }
}
