package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    @DisplayName("정상적인 전화번호로 User 생성")
    void createUser_success() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .phoneNumber("010-1234-5678")
                .build();

        assertThat(user.getPhoneNumber()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("전화번호 형식이 틀리면 예외 발생")
    void createUser_invalidFormat() {
        assertThatThrownBy(() -> User.builder()
                .userId(UUID.randomUUID())
                .phoneNumber("010-123-5678") // 중간 네 자리가 아님
                .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("전화번호 형식은 010-1234-5678 형태여야 합니다.");
    }

    @Test
    @DisplayName("전화번호가 null이면 예외 발생 (선택 사항)")
    void createUser_phoneNumber_null() {
        assertThatThrownBy(() -> User.builder()
                .userId(UUID.randomUUID())
                .phoneNumber(null)
                .build()
        ).isInstanceOf(IllegalArgumentException.class); // 예외 발생 가능
    }
}