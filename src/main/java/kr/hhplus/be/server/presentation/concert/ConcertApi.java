package kr.hhplus.be.server.presentation.concert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.concert.object.ConcertRequest;
import kr.hhplus.be.server.presentation.concert.object.ConcertResponse;
import kr.hhplus.be.server.presentation.concert.object.DateRequest;
import kr.hhplus.be.server.presentation.concert.object.HallResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "🎵 콘서트 조회 API", description = "공연/공연장/날짜 선택 관련 API")
@RequestMapping("/concert")
public interface ConcertApi {

    @Operation(summary = "공연 목록 조회", description = "등록된 전체 콘서트 목록을 조회합니다.")
    @GetMapping("/concertList")
    ResponseEntity<List<ConcertResponse>> concertList();

    @Operation(summary = "공연장 목록 조회", description = "특정 공연 ID에 해당하는 공연장 목록을 조회합니다.")
    @GetMapping("/hallList")
    ResponseEntity<List<HallResponse>> hallList(@RequestParam Integer concertId);

    @Operation(summary = "공연 날짜 목록 조회", description = "선택한 공연 + 공연장 조합에 해당하는 날짜 리스트를 조회합니다.")
    @GetMapping("/dateList")
    ResponseEntity<List<LocalDateTime>> dateList(@Valid @RequestBody DateRequest dateRequest);

    @Operation(summary = "공연 선택 완료", description = "공연 / 공연장 / 날짜 선택 정보를 서버에 제출합니다.")
    @GetMapping("/submit")
    ResponseEntity.BodyBuilder submit(@Valid @RequestBody ConcertRequest request);
}