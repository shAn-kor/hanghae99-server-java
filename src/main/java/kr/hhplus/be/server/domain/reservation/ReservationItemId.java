package kr.hhplus.be.server.domain.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode
@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReservationItemId implements Serializable {

        @Column(name = "reservation_id")
        private Long reservationId;

        @Column(name = "seat_id")
        private Long seatId;
}
