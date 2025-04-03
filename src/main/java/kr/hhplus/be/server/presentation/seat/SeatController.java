package kr.hhplus.be.server.presentation.seat;

import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.seat.object.SeatRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/seat")
public class SeatController {
    @GetMapping("/emptySeatList")
    public ResponseEntity<List<Integer>> emptySeatList(@Valid @RequestBody SeatRequest seatRequest) {
        List<Integer> list = new ArrayList<>();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
