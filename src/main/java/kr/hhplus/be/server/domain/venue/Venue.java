package kr.hhplus.be.server.domain.venue;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "venue")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id") // 이 컬럼명이 반드시 있어야 합니다!
    private Long venueId;

    @Column(name = "venue_name", columnDefinition = "varchar(50)")
    private String venueName;

    @Column(name = "address", columnDefinition = "varchar(100)")
    private String address;

    @Column(name = "seat_count", columnDefinition = "int")
    private Integer seatCount;

    @Builder
    public Venue(String venueName, String address, Integer seatCount) {
        if (seatCount == null || seatCount <= 0 || seatCount > 50) {
            throw new IllegalArgumentException("seatCount must be between 1 and 50");
        }
        this.venueName = venueName;
        this.address = address;
        this.seatCount = seatCount;
    }
}