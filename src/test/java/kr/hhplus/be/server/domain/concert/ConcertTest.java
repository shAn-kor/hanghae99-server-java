package kr.hhplus.be.server.domain.concert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class ConcertTest {

    @Test
    @DisplayName("정상적인 concertId로 객체 생성 성공")
    void createConcert_success() {
        Concert concert = Concert.builder()
                .concertName("Awesome Concert")
                .artist("Cool Artist")
                .build();

        assertThat(concert.getConcertName()).isEqualTo("Awesome Concert");
        assertThat(concert.getArtist()).isEqualTo("Cool Artist");
    }

    @Test
    @DisplayName("concertId가 null이면 예외 발생")
    void createConcert_concertId_null() {
        assertThatThrownBy(() -> Concert.builder()
                .concertName("Null Test")
                .artist("Test Artist")
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("concertId must not be null");
    }

    @Test
    @DisplayName("concertId가 0 이하이면 예외 발생")
    void createConcert_concertId_zeroOrNegative() {
        assertThatThrownBy(() -> Concert.builder()
                .concertName("Zero Test")
                .artist("Test Artist")
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("concertId must be positive");

        assertThatThrownBy(() -> Concert.builder()
                .concertName("Negative Test")
                .artist("Test Artist")
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("concertId must be positive");
    }
}