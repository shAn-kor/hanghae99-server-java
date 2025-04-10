package kr.hhplus.be.server.presentation.seat;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.presentation.seat.object.SeatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seat")
public class SeatController implements SeatApi {
    private final SeatService seatService;

    @PostMapping("/emptySeatList")
    public ResponseEntity<List<Integer>> emptySeatList(@Valid @RequestBody SeatRequest seatRequest) {
        List<Integer> list = seatService.getEmptySeats().stream().map(Seat::seatNumber).toList();

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
