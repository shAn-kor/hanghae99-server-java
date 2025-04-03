package kr.hhplus.be.server.presentation.reservation;

import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.reservation.object.ReservationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/reservation")
public class ReservationController implements ReservationApi {
    @PostMapping("/reserve")
    public BodyBuilder reserve(@Valid @RequestBody ReservationRequest request) {
        return ok();
    }
}
