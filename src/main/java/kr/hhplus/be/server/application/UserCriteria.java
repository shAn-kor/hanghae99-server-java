package kr.hhplus.be.server.application;

import kr.hhplus.be.server.presentation.token.object.GenerateTokenRequest;
import lombok.Builder;

@Builder
public record UserCriteria(
        String phoneNumber,
        Long concertId
) {
    public static UserCriteria from (GenerateTokenRequest request) {
        return UserCriteria.builder().phoneNumber(request.phoneNumber()).concertId(request.concertId()).build();
    }
}
