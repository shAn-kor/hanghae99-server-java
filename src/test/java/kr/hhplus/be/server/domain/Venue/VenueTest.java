package kr.hhplus.be.server.domain.Venue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class VenueTest {

    @Test
    @DisplayName("정상적인 값으로 Venue 생성")
    void createVenue_success() {
        Venue venue = Venue.builder()
                .venueId(1L)
                .venueName("올림픽홀")
                .address("서울 송파구")
                .seatCount(30)
                .build();

        assertThat(venue.venueId()).isEqualTo(1L);
        assertThat(venue.seatCount()).isEqualTo(30);
    }

    @Test
    @DisplayName("venueId가 null이면 예외 발생")
    void createVenue_venueId_null() {
        assertThatThrownBy(() -> Venue.builder()
                .venueId(null)
                .venueName("Test")
                .address("Test")
                .seatCount(10)
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("venueId is null");
    }

    @Test
    @DisplayName("venueId가 0 이하이면 예외 발생")
    void createVenue_venueId_invalid() {
        assertThatThrownBy(() -> Venue.builder()
                .venueId(0L)
                .venueName("Test")
                .address("Test")
                .seatCount(10)
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("venueId must be greater than 0");
    }

    @Test
    @DisplayName("seatCount가 null이면 예외 발생")
    void createVenue_seatCount_null() {
        assertThatThrownBy(() -> Venue.builder()
                .venueId(1L)
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
                .venueId(1L)
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
                .venueId(1L)
                .venueName("Test")
                .address("Test")
                .seatCount(51)
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("seatCount must be less than 50");
    }
}