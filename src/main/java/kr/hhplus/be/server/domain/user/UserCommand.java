package kr.hhplus.be.server.domain.user;

import lombok.Builder;

@Builder
public record UserCommand(
        String phoneNumber
) {
}
