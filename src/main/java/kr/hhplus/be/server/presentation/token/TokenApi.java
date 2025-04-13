package kr.hhplus.be.server.presentation.token;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.token.object.GenerateTokenRequest;
import kr.hhplus.be.server.presentation.token.object.TokenRequest;
import kr.hhplus.be.server.presentation.token.object.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "🔐 대기열 토큰 API", description = "유저 대기열 토큰 발급 및 상태 조회 API")
@RequestMapping("/token")
public interface TokenApi {

    @Operation(summary = "대기열 토큰 발급", description = "유저 UUID를 기반으로 대기열 토큰을 발급합니다.")
    @GetMapping("/getToken")
    ResponseEntity.BodyBuilder getToken(@Valid @RequestBody GenerateTokenRequest request);

    @Operation(summary = "대기열 상태 조회", description = "현재 토큰의 대기 순번 또는 상태를 반환합니다.")
    @PostMapping("/status")
    ResponseEntity<TokenResponse> getStatus(@Valid @RequestBody TokenRequest tokenRequest);
}