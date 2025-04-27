package kr.hhplus.be.server.presentation.reservation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.ReservationFacade;
import kr.hhplus.be.server.application.ReservationItemResult;
import kr.hhplus.be.server.domain.reservation.ReservationCommand;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;

import static org.springframework.http.ResponseEntity.BodyBuilder;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController implements ReservationApi {
    private final ReservationFacade reservationFacade;
    private final ReservationService reservationService;

    @Override
    public ReservationResponse emptyseats(@Valid @RequestBody ReservationRequest request) {
        ReservationItemResult result = reservationFacade.getEmptySeat(ReservationRequest.toCriteria(request));
        return ReservationResponse.builder().seatIdList(result.seatIdList()).build();
    }

    @PostMapping("/reserve")
    public BodyBuilder reserve(@Valid @RequestBody ReservationRequest request) {
        try {
            reservationFacade.reserveSeats(ReservationRequest.toCriteria(request));
        } catch (AccessDeniedException e) {
            return ResponseEntity.badRequest();
        }
        return ok();
    }

    @Override
    public BodyBuilder unReserve(ReservationRequest request) {
        ReservationCommand command = ReservationCommand.builder().userId(request.uuid()).concertScheduleId(request.concertDateTimeId()).build();
        reservationService.unReserve(command);
        return ok();
    }
}
