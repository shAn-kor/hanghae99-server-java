package kr.hhplus.be.server.domain.user;

import lombok.Builder;

import java.util.UUID;

@Builder
public record User(
        UUID userId,
        String phoneNumber
) {
    public User {
        if (phoneNumber == null || !phoneNumber.matches("^010-\\d{4}-\\d{4}$")) {
            throw new IllegalArgumentException("전화번호 형식은 010-1234-5678 형태여야 합니다.");
        }
    }
}
