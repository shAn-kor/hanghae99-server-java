package kr.hhplus.be.server.presentation.token;

import kr.hhplus.be.server.application.TokenFacade;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenCommand;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.presentation.token.object.GenerateTokenRequest;
import kr.hhplus.be.server.presentation.token.object.TokenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TokenControllerTest {

    private TokenFacade tokenFacade;
    private TokenService tokenService;
    private TokenController tokenController;

    @BeforeEach
    void setUp() {
        tokenFacade = mock(TokenFacade.class);
        tokenService = mock(TokenService.class);
        tokenController = new TokenController(tokenFacade, tokenService);
    }

    @Test
    @DisplayName("getToken()은 유저 전화번호로 토큰을 생성 요청한다")
    void getToken_success() {
        // given
        GenerateTokenRequest request = new GenerateTokenRequest("010-1234-5678");

        // when
        var response = tokenController.getToken(request);

        // then
        verify(tokenFacade).createToken(argThat(criteria ->
                criteria.phoneNumber().equals("010-1234-5678")));
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("POST /token/status - 유저 ID로 토큰 상태 조회")
    void getStatus_success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID tokenId = UUID.randomUUID();
        TokenRequest request = new TokenRequest(userId);

        Token mockToken = new Token( userId, 5, true, LocalDateTime.now());

        // ✅ 여기에서 ResponseEntity 말고 Token 자체를 반환해야 함
        when(tokenService.getToken(new TokenCommand(userId))).thenReturn(mockToken);

        // when
        var response = tokenController.getStatus(request);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().userId()).isEqualTo(userId);
        assertThat(response.getBody().tokenId()).isEqualTo(tokenId);
        assertThat(response.getBody().position()).isEqualTo(5);
        assertThat(response.getBody().valid()).isTrue();
    }
}