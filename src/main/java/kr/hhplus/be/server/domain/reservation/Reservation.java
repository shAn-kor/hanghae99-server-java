package kr.hhplus.be.server.domain.reservation;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "reservation")
@Table(name = "reservation", indexes = {
        @Index(name = "idx_reservation_deadline_status", columnList = "status, created_at")
})
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(name = "user_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "concert_schedule_id", nullable = false, updatable = false, columnDefinition = "bigint")
    private Long concertScheduleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(10)")
    private ReservationStatus status;

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "timestamp", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany
    private List<ReservationItem> reservationItems;

    @Builder
    public Reservation(UUID userId, ReservationStatus status, Long concertScheduleId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        if (concertScheduleId == null) {
            throw new IllegalArgumentException("concertScheduleId is null");
        }

        this.userId = userId;
        this.concertScheduleId = concertScheduleId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}