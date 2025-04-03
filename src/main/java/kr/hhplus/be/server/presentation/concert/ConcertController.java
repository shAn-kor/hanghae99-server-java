package kr.hhplus.be.server.presentation.concert;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.concert.object.ConcertRequest;
import kr.hhplus.be.server.presentation.concert.object.ConcertResponse;
import kr.hhplus.be.server.presentation.concert.object.DateRequest;
import kr.hhplus.be.server.presentation.concert.object.HallResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/concert")
public class ConcertController implements ConcertApi {
    @GetMapping("/concertList")
    public ResponseEntity<List<ConcertResponse>> concertList() {
        List<ConcertResponse> responseList = new ArrayList<>();
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @GetMapping("/hallList")
    public ResponseEntity<List<HallResponse>> hallList(@RequestParam Integer concertId) {
        List<HallResponse> responseList = new ArrayList<>();
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @GetMapping("/dateList")
    public ResponseEntity<List<LocalDateTime>> dateList(@Valid @RequestBody DateRequest dateRequest) {
        List<LocalDateTime> responseList = new ArrayList<>();
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @GetMapping("/submit")
    public ResponseEntity.BodyBuilder submit(@Valid @RequestBody ConcertRequest request) {
        return ok();
    }
}
