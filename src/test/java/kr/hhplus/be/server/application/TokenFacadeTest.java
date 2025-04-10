package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.dto.TokenResult;
import kr.hhplus.be.server.application.dto.UserCriteria;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenCommand;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TokenFacadeTest {

    private PointService pointService;
    private UserService userService;
    private TokenService tokenService;
    private TokenFacade tokenFacade;

    @BeforeEach
    void setUp() {
        pointService = mock(PointService.class);
        userService = mock(UserService.class);
        tokenService = mock(TokenService.class);
        tokenFacade = new TokenFacade(pointService, userService, tokenService);
    }

    @Test
    @DisplayName("createToken - 성공적으로 토큰을 생성하고 응답을 반환한다")
    void createToken_success() throws InsufficientBalanceException {
        // given
        String phone = "010-1234-5678";
        UserCriteria criteria = new UserCriteria(phone);

        UUID userId = UUID.randomUUID();
        UUID tokenId = UUID.randomUUID();
        Token token = new Token(tokenId, userId, 5, true, LocalDateTime.now());

        when(userService.getUserId(any(UserCommand.class))).thenReturn(userId);
        when(pointService.checkPoint(any(PointCommand.class))).thenReturn(true);
        when(tokenService.generateToken(any(TokenCommand.class))).thenReturn(token);

        // when
        TokenResult result = tokenFacade.createToken(criteria);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.position()).isEqualTo(token.position());
        assertThat(result.valid()).isTrue();

        verify(userService, times(1)).getUserId(any());
        verify(pointService, times(1)).checkPoint(any());
        verify(tokenService, times(1)).generateToken(any());
    }

    @Test
    @DisplayName("createToken - 포인트가 없으면 예외 발생")
    void createToken_insufficientBalance() {
        // given
        String phone = "010-1234-5678";
        UserCriteria criteria = new UserCriteria(phone);

        UUID userId = UUID.randomUUID();

        when(userService.getUserId(any())).thenReturn(userId);
        when(pointService.checkPoint(any())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> tokenFacade.createToken(criteria))
                .isInstanceOf(InsufficientBalanceException.class);

        verify(userService).getUserId(any());
        verify(pointService).checkPoint(any());
        verify(tokenService, never()).generateToken(any());
    }
}