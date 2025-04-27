package kr.hhplus.be.server.presentation.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "🎟️ 좌석 예약 API", description = "좌석 임시 예약 처리 API")
@RequestMapping("/reservation")
public interface ReservationApi {

    @Operation(summary = "빈 좌석 조회", description = "유저가 고를 빈 좌석 목록을 반환합니다.")
    @GetMapping("/emptyseats")
    ReservationResponse emptyseats(@Valid @RequestBody ReservationRequest request);

    @Operation(summary = "좌석 예약 요청", description = "유저가 선택한 공연 날짜에 대해 최대 4개의 좌석을 임시로 예약합니다. 임시 배정 시간은 3분입니다.")
    @PostMapping("/reserve")
    ResponseEntity.BodyBuilder reserve(@Valid @RequestBody ReservationRequest request);

    @Operation(summary = "좌석 예약 취소 요청", description = "유저가 주문을 취소 합니다.")
    @PostMapping("/unreserve")
    ResponseEntity.BodyBuilder unReserve(@Valid @RequestBody ReservationRequest request);
}