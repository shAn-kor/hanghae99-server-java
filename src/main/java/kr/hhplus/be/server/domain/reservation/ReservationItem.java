package kr.hhplus.be.server.domain.reservation;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@IdClass(ReservationItemPK.class)
@Entity(name = "reservation_item")
@Table(name = "reservation_item", indexes = {
        @Index(name = "idx_reservation_item_reservation_id", columnList = "reservation_id")
})
@NoArgsConstructor
@Setter
@Getter
public class ReservationItem {

    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    @Id
    @Column(name = "seat_id")
    private Long seatId;

    @Builder
    public ReservationItem(Reservation reservation, Long seatId) {
        this.reservation = reservation;
        this.seatId = seatId;
    }
}