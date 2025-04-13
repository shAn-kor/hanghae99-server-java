package kr.hhplus.be.server.presentation.user.object;


import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UserRequest(
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = "전화번호 형식은 010-1234-5678 형태여야 합니다."
        )
        String phoneNumber
) {
}
