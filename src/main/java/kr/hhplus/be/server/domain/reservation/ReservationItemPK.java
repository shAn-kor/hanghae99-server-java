package kr.hhplus.be.server.domain.reservation;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReservationItemPK {
        Long reservationId;
        Long seatId;
}
