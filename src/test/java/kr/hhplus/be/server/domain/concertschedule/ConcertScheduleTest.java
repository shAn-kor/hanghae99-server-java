package kr.hhplus.be.server.domain.concertschedule;

import kr.hhplus.be.server.domain.venue.Venue;
import kr.hhplus.be.server.infrastructure.repository.ConcertScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ConcertScheduleTest {
    private ConcertScheduleRepository concertScheduleRepository;

    @BeforeEach
    void setUp() {
        concertScheduleRepository = mock(ConcertScheduleRepository.class);
    }

    @Test
    @DisplayName("concertId가 null이면 예외 발생")
    void createConcertDate_concertId_null() {
        Venue venue = mock(Venue.class);
        assertThatThrownBy(() -> ConcertSchedule.builder()
                .concertId(1L)
                .venueId(2L)
                .concertDate(LocalDateTime.now().plusDays(1))
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("concertId must not be null");
    }

    @Test
    @DisplayName("concertDate가 과거이면 예외 발생")
    void createConcertDate_concertDate_inPast() {
        assertThatThrownBy(() -> ConcertSchedule.builder()
                .concertId(1L)
                .venueId(2L)
                .concertDate(LocalDateTime.now().minusMinutes(1))
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("concertDate must be before now");
    }
}