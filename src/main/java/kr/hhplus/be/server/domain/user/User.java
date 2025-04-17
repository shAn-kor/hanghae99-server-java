package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    private UUID userId;

    @Column(name = "phone_number", columnDefinition = "char(13)", nullable = false)
    private String phoneNumber;

    @Builder
    public User(UUID userId, String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("^010-\\d{4}-\\d{4}$")) {
            throw new IllegalArgumentException("전화번호 형식은 010-1234-5678 형태여야 합니다.");
        }

        this.userId = userId != null ? userId : UUID.randomUUID();
        this.phoneNumber = phoneNumber;
    }
}