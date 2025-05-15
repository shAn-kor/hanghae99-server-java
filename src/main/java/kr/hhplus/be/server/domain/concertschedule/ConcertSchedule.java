package kr.hhplus.be.server.domain.concertschedule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
    private Long concertScheduleId;

    @Column(name = "concert_id", columnDefinition = "bigint", updatable = false, nullable = false)
    private Long concertId;

    @Column(name = "venue_id", columnDefinition = "bigint", updatable = false)
    private Long venueId;

    @Column(name = "date", columnDefinition = "timestamp", updatable = false)
    @CreatedDate
    private LocalDateTime concertDate;

    @Builder
    public ConcertSchedule(Long concertId, Long venueId, LocalDateTime concertDate) {
        if (concertId == null) {
            throw new IllegalArgumentException("concert must not be null");
        }

        if (concertDate != null && concertDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("concertDate must be before now");
        }

        this.concertId = concertId;
        this.venueId = venueId;
        this.concertDate = concertDate;
    }
}