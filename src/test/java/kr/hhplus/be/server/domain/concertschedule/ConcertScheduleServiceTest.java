package kr.hhplus.be.server.domain.concertschedule;

import kr.hhplus.be.server.domain.Venue.Venue;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertCommand;
import kr.hhplus.be.server.infrastructure.repository.ConcertScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ConcertScheduleServiceTest {

    private ConcertScheduleRepository concertScheduleRepository;
    private ConcertScheduleService concertScheduleService;

    @BeforeEach
    void setUp() {
        concertScheduleRepository = mock(ConcertScheduleRepository.class);
        concertScheduleService = new ConcertScheduleService(concertScheduleRepository);
    }

    @Test
    @DisplayName("concertId로 concertDate 리스트를 조회한다")
    void getConcertDates_success() {
        // given
        Long concertId = 1L;
        Long venueId = 2L;
        Concert concert = mock(Concert.class);
        Venue venue = mock(Venue.class);
        ConcertCommand command = new ConcertCommand(concertId, 1L);

        List<ConcertSchedule> mockResult = List.of(
                ConcertSchedule.builder()
                        .concertId(concertId)
                        .venueId(venueId)
                        .concertDate(LocalDateTime.of(2025, 5, 10, 20, 0))
                        .build(),
                ConcertSchedule.builder()
                        .concertId(concertId)
                        .venueId(venueId)
                        .concertDate(LocalDateTime.of(2025, 5, 11, 20, 0))
                        .build()
        );

        when(concertScheduleRepository.getConcertDates(concertId)).thenReturn(mockResult);

        // when
        List<ConcertSchedule> result = concertScheduleService.getConcertDates(command);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(mockResult);
        verify(concertScheduleRepository, times(1)).getConcertDates(concertId);
    }
}