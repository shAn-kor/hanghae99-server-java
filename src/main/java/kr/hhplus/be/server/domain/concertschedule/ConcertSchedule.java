package kr.hhplus.be.server.domain.concertschedule;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.Venue.Venue;
import kr.hhplus.be.server.domain.concert.Concert;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Entity(name = "concert_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertSchedule {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", referencedColumnName = "concert_id")
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", referencedColumnName = "venue_id")
    private Venue venue;

    @Column(name = "date", columnDefinition = "timestamp", updatable = false)
    @CreatedDate
    private LocalDateTime concertDate;

    @Builder
    public ConcertSchedule(Concert concert, Venue venue, LocalDateTime concertDate) {
        if (concert == null) {
            throw new IllegalArgumentException("concert must not be null");
        }

        if (concertDate != null && concertDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("concertDate must be before now");
        }

        this.concert = concert;
        this.venue = venue;
        this.concertDate = concertDate;
    }
}