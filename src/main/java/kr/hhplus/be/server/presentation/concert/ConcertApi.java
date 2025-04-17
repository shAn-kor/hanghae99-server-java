package kr.hhplus.be.server.presentation.concert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.concert.object.ConcertDateResponse;
import kr.hhplus.be.server.presentation.concert.object.ConcertRequest;
import kr.hhplus.be.server.presentation.concert.object.ConcertResponse;
import kr.hhplus.be.server.presentation.concert.object.VenueResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "🎵 콘서트 조회 API", description = "공연/공연장/날짜 선택 관련 API")
@RequestMapping("/concert")
public interface ConcertApi {

    @Operation(summary = "공연 목록 조회", description = "등록된 전체 콘서트 목록을 조회합니다.")
    @GetMapping("/concertList")
    ResponseEntity<List<ConcertResponse>> concertList();

    @Operation(summary = "공연장 목록 조회", description = "특정 공연 ID에 해당하는 공연장 목록을 조회합니다.")
    @GetMapping("/hallList")
    ResponseEntity<VenueResponse> hallList(@Valid @RequestBody ConcertRequest request);

    @Operation(summary = "공연 날짜 목록 조회", description = "선택한 공연 + 공연장 조합에 해당하는 날짜 리스트를 조회합니다.")
    @GetMapping("/dateList")
    ResponseEntity<List<ConcertDateResponse>> dateList(@Valid @RequestBody ConcertRequest request);
}