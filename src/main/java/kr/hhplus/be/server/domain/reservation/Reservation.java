package kr.hhplus.be.server.domain.reservation;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "reservation")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(name = "user_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(10)")
    private ReservationStatus status;

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "timestamp", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Reservation(UUID userId, ReservationStatus status) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        this.userId = userId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}