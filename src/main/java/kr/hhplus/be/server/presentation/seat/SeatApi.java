package kr.hhplus.be.server.presentation.seat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.seat.object.SeatRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "🪑 좌석 조회 API", description = "예약 가능한 빈 좌석 목록을 조회하는 API")
@RequestMapping("/seat")
public interface SeatApi {

    @Operation(summary = "빈 좌석 목록 조회", description = "공연 날짜 ID를 기준으로 예약 가능한 좌석 번호 리스트를 조회합니다.")
    @PostMapping("/emptySeatList")
    ResponseEntity<List<Integer>> emptySeatList(@Valid @RequestBody SeatRequest seatRequest);
}