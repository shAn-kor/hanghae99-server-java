package kr.hhplus.be.server.presentation.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.reservation.object.ReservationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "🎟️ 좌석 예약 API", description = "좌석 임시 예약 처리 API")
@RequestMapping("/reservation")
public interface ReservationApi {

    @Operation(summary = "좌석 예약 요청", description = "유저가 선택한 공연 날짜에 대해 최대 4개의 좌석을 임시로 예약합니다. 임시 배정 시간은 3분입니다.")
    @PostMapping("/reserve")
    ResponseEntity.BodyBuilder reserve(@Valid @RequestBody ReservationRequest request);
}