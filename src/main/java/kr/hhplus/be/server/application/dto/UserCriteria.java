package kr.hhplus.be.server.application.dto;

import kr.hhplus.be.server.presentation.token.object.GenerateTokenRequest;
import lombok.Builder;

@Builder
public record UserCriteria(
        String phoneNumber
) {
    public static UserCriteria from (GenerateTokenRequest request) {
        return UserCriteria.builder().phoneNumber(request.phoneNumber()).build();
    }
}
