package kr.hhplus.be.server.presentation.user;

import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.presentation.user.object.UserRequest;
import kr.hhplus.be.server.presentation.user.object.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerUnitTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    @DisplayName("getUuid()는 전화번호에 해당하는 UUID를 반환한다")
    void getUuid_success() {
        // given
        String phoneNumber = "010-1234-5678";
        UUID expectedUuid = UUID.randomUUID();
        UserRequest request = new UserRequest(phoneNumber);

        when(userService.getUserId(UserCommand.builder().phoneNumber(phoneNumber).build()))
                .thenReturn(expectedUuid);

        // when
        ResponseEntity<UserResponse> response = userController.getUuid(request);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().uuid()).isEqualTo(expectedUuid);

        verify(userService).getUserId(any(UserCommand.class));
    }
}