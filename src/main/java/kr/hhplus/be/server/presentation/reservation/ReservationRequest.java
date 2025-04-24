package kr.hhplus.be.server.presentation.reservation;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.application.dto.ReservationCriteria;

import java.util.List;
import java.util.UUID;

public record ReservationRequest(
        @NotNull UUID uuid,
        @NotNull Long concertDateTimeId,
        @NotNull List<Integer> seatList
        ) {
        public static ReservationCriteria toCriteria(@NotNull ReservationRequest request) {
                return ReservationCriteria.builder().uuid(request.uuid).seatList(request.seatList).build();
        }
}
