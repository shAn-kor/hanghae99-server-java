package kr.hhplus.be.server.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.user.object.UserRequest;
import kr.hhplus.be.server.presentation.user.object.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "🙋‍ 사용자 API", description = "전화번호 기반 유저 조회 또는 UUID 발급 API")
@RequestMapping("/user")
public interface UserApi {

    @Operation(summary = "UUID 조회 또는 발급", description = "전화번호로 기존 유저의 UUID를 조회하거나 없을 경우 새로 생성합니다.")
    @PostMapping("/uuid")
    ResponseEntity<UserResponse> getUuid(@Valid @RequestBody UserRequest user);
}