package kr.hhplus.be.server.presentation.reservation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.ReservationFacade;
import kr.hhplus.be.server.presentation.reservation.object.ReservationRequest;
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

    @PostMapping("/reserve")
    public BodyBuilder reserve(@Valid @RequestBody ReservationRequest request) {
        try {
            reservationFacade.reserveSeats(ReservationRequest.toCriteria(request));
        } catch (AccessDeniedException e) {
            return ResponseEntity.badRequest();
        }
        return ok();
    }
}
