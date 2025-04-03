package kr.hhplus.be.server.presentation.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.point.object.PointRequest;
import kr.hhplus.be.server.presentation.point.object.PointResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "💰 포인트 API", description = "포인트 잔액 확인, 충전, 사용 내역 조회")
@RequestMapping("/point")
public interface PointApi {

    @Operation(summary = "포인트 잔액 조회", description = "유저의 현재 포인트 잔액을 조회합니다.")
    @GetMapping("/getPoint")
    ResponseEntity<PointResponse> getPoint(@Valid @RequestBody PointRequest pointRequest);

    @Operation(summary = "포인트 충전", description = "특정 유저의 포인트를 지정된 금액만큼 충전합니다.")
    @PostMapping("/charge")
    ResponseEntity<PointResponse> charge(@Valid @RequestBody PointRequest pointRequest);

    @Operation(summary = "포인트 내역 조회", description = "특정 유저의 포인트 사용/충전 이력을 조회합니다.")
    @GetMapping("/history")
    ResponseEntity<List<PointResponse>> history(@Valid @RequestBody PointRequest pointRequest);
}