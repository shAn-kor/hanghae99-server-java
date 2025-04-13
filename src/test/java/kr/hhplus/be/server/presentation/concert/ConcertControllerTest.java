package kr.hhplus.be.server.presentation.concert;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ConcertControllerUnitTest {

    private ConcertService concertService;
    private VenueService venueService;
    private ConcertDateService concertDateService;
    private ConcertController controller;

    @BeforeEach
    void setUp() {
        concertService = mock(ConcertService.class);
        venueService = mock(VenueService.class);
        concertDateService = mock(ConcertDateService.class);
        controller = new ConcertController(concertService, venueService, concertDateService);
    }

    @Test
    @DisplayName("concertList()는 콘서트 리스트를 반환한다")
    void concertList_success() {
        // given
        List<Concert> concerts = List.of(
                new Concert(1L, "콘서트A", "아티스트A"),
                new Concert(2L, "콘서트B", "아티스트B")
        );
        when(concertService.concertList()).thenReturn(concerts);

        // when
        ResponseEntity<List<ConcertResponse>> response = controller.concertList();

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).concertName()).isEqualTo("콘서트A");
    }

    @Test
    @DisplayName("hallList()는 공연장 리스트를 반환한다")
    void hallList_success() {
        // given
        ConcertRequest request = ConcertRequest.builder().concertId(1L).build();
        List<Venue> venues = List.of(
                new Venue(1L, "올림픽홀", "서울", 30)
        );
        when(venueService.getConcertVenueList(request.toCommand())).thenReturn(venues);

        // when
        ResponseEntity<List<VenueResponse>> response = controller.hallList(request);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).venueName()).isEqualTo("올림픽홀");
    }

    @Test
    @DisplayName("dateList()는 콘서트 날짜 리스트를 반환한다")
    void dateList_success() {
        // given
        ConcertRequest request = ConcertRequest.builder().concertId(1L).build();
        List<ConcertDate> dates = List.of(
                ConcertDate.builder()
                        .concertId(1L)
                        .venueId(1L)
                        .concertDate(LocalDateTime.of(2025, 5, 1, 20, 0))
                        .build()
        );
        when(concertDateService.getConcertDates(request.toCommand())).thenReturn(dates);

        // when
        ResponseEntity<List<ConcertDateResponse>> response = controller.dateList(request);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).concertId()).isEqualTo(1L);
    }
}