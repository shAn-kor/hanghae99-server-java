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

    @Column(name = "concert_schedule_id", columnDefinition = "bigint", nullable = false)
    private Long concertScheduleId;

    @Column(name = "seat_number", columnDefinition = "int", nullable = false)
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(10)", nullable = false)
    private SeatStatus status;

    @Builder
    public Seat(Long concertScheduleId, Integer seatNumber, SeatStatus status) {
        if (concertScheduleId == null) {
            throw new IllegalArgumentException("concertDateId must not be null");
        }
        if (seatNumber == null || seatNumber <= 0 || seatNumber > 50) {
            throw new IllegalArgumentException("seatNumber must be between 1 and 50");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }

        this.concertScheduleId = concertScheduleId;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    public void reserve() {
        this.status = SeatStatus.RESERVED;
    }

    public void unReserve() {
        this.status = SeatStatus.EMPTY;
    }
}