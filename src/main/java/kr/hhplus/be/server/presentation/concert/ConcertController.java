package kr.hhplus.be.server.presentation.concert;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.Venue.Venue;
import kr.hhplus.be.server.domain.Venue.VenueService;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertService;
import kr.hhplus.be.server.domain.concertdate.ConcertDate;
import kr.hhplus.be.server.domain.concertdate.ConcertDateService;
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
    private final ConcertDateService concertDateService;

    @GetMapping("/concertList")
    public ResponseEntity<List<ConcertResponse>> concertList() {
        List<Concert> concertList = concertService.concertList();
        List<ConcertResponse> responseList = ConcertResponse.fromConcertList(concertList);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @PostMapping("/hallList")
    public ResponseEntity<List<VenueResponse>> hallList(@Valid @RequestBody ConcertRequest request) {
        List<Venue> venueList = venueService.getConcertVenueList(request.toCommand());
        List<VenueResponse> responseList = VenueResponse.from(venueList);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @PostMapping("/dateList")
    public ResponseEntity<List<ConcertDateResponse>> dateList(@Valid @RequestBody ConcertRequest request) {
        List<ConcertDate> concertDateList = concertDateService.getConcertDates(request.toCommand());
        List<ConcertDateResponse> responseList = ConcertDateResponse.from(concertDateList);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}
