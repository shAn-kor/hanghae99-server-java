package kr.hhplus.be.server.domain.venue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class VenueTest {

    @Test
    @DisplayName("정상적인 값으로 Venue 생성")
    void createVenue_success() {
        Venue venue = Venue.builder()
                .venueName("올림픽홀")
                .address("서울 송파구")
                .seatCount(30)
                .build();

        assertThat(venue.getSeatCount()).isEqualTo(30);
    }

    @Test
    @DisplayName("seatCount가 null이면 예외 발생")
    void createVenue_seatCount_null() {
        assertThatThrownBy(() -> Venue.builder()
                .venueName("Test")
                .address("Test")
                .seatCount(null)
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("seatCount is null");
    }

    @Test
    @DisplayName("seatCount가 0 이하이면 예외 발생")
    void createVenue_seatCount_zeroOrNegative() {
        assertThatThrownBy(() -> Venue.builder()
                .venueName("Test")
                .address("Test")
                .seatCount(0)
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("seatCount must be greater than 0");
    }

    @Test
    @DisplayName("seatCount가 50 초과이면 예외 발생")
    void createVenue_seatCount_exceedsLimit() {
        assertThatThrownBy(() -> Venue.builder()
                .venueName("Test")
                .address("Test")
                .seatCount(51)
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("seatCount must be less than 50");
    }
}