package kr.hhplus.be.server.domain.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@IdClass(ReservationItemPK.class)
@Entity(name = "reservation_item")
@NoArgsConstructor
@Setter
@Getter
public class ReservationItem {

    @Id
    @Column(name = "reservation_id")
    private Long reservationId;

    @Id
    @Column(name = "seat_id")
    private Long seatId;

    @Builder
    public ReservationItem(Long reservationId, Long seatId) {
        this.reservationId = reservationId;
        this.seatId = seatId;
    }
}