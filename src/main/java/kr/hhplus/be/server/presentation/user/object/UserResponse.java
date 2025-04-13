package kr.hhplus.be.server.presentation.user.object;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse (
        UUID uuid
) {
}
