package kr.hhplus.be.server.domain.token;

import kr.hhplus.be.server.application.TokenFacade;
import kr.hhplus.be.server.presentation.token.TokenController;
import kr.hhplus.be.server.presentation.token.object.GenerateTokenRequest;
import kr.hhplus.be.server.presentation.token.object.TokenRequest;
import kr.hhplus.be.server.presentation.token.object.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TokenControllerTest {

    private TokenService tokenService;
    private TokenFacade tokenFacade;
    private TokenController tokenController;

    @BeforeEach
    void setUp() {
        tokenService = mock(TokenService.class);
        tokenFacade = mock(TokenFacade.class);
        tokenController = new TokenController(tokenFacade, tokenService);
    }

    @Test
    @DisplayName("POST /token/getToken - 유저 전화번호로 토큰 생성")
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
        TokenResponse mockResponse = TokenResponse.from(mockToken);

        when(tokenService.getToken(new TokenCommand(userId))).thenReturn(mockToken);

        // when
        var response = tokenController.getStatus(request);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(mockResponse);
    }
}