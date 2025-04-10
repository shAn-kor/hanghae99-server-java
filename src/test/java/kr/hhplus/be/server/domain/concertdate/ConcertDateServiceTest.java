package kr.hhplus.be.server.domain.concertdate;

import kr.hhplus.be.server.domain.concert.ConcertCommand;
import kr.hhplus.be.server.infrastructure.repository.ConcertDateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ConcertDateServiceTest {

    private ConcertDateRepository concertDateRepository;
    private ConcertDateService concertDateService;

    @BeforeEach
    void setUp() {
        concertDateRepository = mock(ConcertDateRepository.class);
        concertDateService = new ConcertDateService(concertDateRepository);
    }

    @Test
    @DisplayName("concertId로 concertDate 리스트를 조회한다")
    void getConcertDates_success() {
        // given
        Long concertId = 1L;
        ConcertCommand command = new ConcertCommand(concertId, 1L);

        List<ConcertDate> mockResult = List.of(
                ConcertDate.builder()
                        .concertId(concertId)
                        .venueId(100L)
                        .concertDate(LocalDateTime.of(2025, 5, 10, 20, 0))
                        .build(),
                ConcertDate.builder()
                        .concertId(concertId)
                        .venueId(101L)
                        .concertDate(LocalDateTime.of(2025, 5, 11, 20, 0))
                        .build()
        );

        when(concertDateRepository.getConcertDates(concertId)).thenReturn(mockResult);

        // when
        List<ConcertDate> result = concertDateService.getConcertDates(command);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(mockResult);
        verify(concertDateRepository, times(1)).getConcertDates(concertId);
    }
}