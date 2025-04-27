package kr.hhplus.be.server.domain.seat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "seat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @Column(name = "venue_id", columnDefinition = "bigint", nullable = false)
    private Long venueId;

    @Column(name = "seat_number", columnDefinition = "int", nullable = false)
    private Integer seatNumber;

    @Builder
    public Seat(Long venueId, Integer seatNumber) {
        if (venueId == null) {
            throw new IllegalArgumentException("concertDateId must not be null");
        }
        if (seatNumber == null || seatNumber <= 0 || seatNumber > 50) {
            throw new IllegalArgumentException("seatNumber must be between 1 and 50");
        }

        this.venueId = venueId;
        this.seatNumber = seatNumber;
    }
}