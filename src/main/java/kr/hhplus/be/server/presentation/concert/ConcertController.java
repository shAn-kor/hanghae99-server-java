package kr.hhplus.be.server.presentation.concert;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.venue.Venue;
import kr.hhplus.be.server.domain.venue.VenueService;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertService;
import kr.hhplus.be.server.domain.concertschedule.ConcertSchedule;
import kr.hhplus.be.server.domain.concertschedule.ConcertScheduleService;
import kr.hhplus.be.server.presentation.concert.object.ConcertDateResponse;
import kr.hhplus.be.server.presentation.concert.object.ConcertRequest;
import kr.hhplus.be.server.presentation.concert.object.ConcertResponse;
import kr.hhplus.be.server.presentation.concert.object.VenueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concert")
public class ConcertController implements ConcertApi {
    private final ConcertService concertService;
    private final VenueService venueService;
    private final ConcertScheduleService concertScheduleService;

    @GetMapping("/concertList")
    public ResponseEntity<List<ConcertResponse>> concertList() {
        List<Concert> concertList = concertService.concertList();
        List<ConcertResponse> responseList = ConcertResponse.fromConcertList(concertList);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @PostMapping("/hallList")
    public ResponseEntity<VenueResponse> hallList(@Valid @RequestBody ConcertRequest request) {
        Venue venue = venueService.getConcertVenue(request.toCommand());
        VenueResponse response = VenueResponse.from(venue);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/dateList")
    public ResponseEntity<List<ConcertDateResponse>> dateList(@Valid @RequestBody ConcertRequest request) {
        List<ConcertSchedule> concertScheduleList = concertScheduleService.getConcertDates(request.toCommand());
        List<ConcertDateResponse> responseList = ConcertDateResponse.from(concertScheduleList);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}
