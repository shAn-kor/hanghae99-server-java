package kr.hhplus.be.server.presentation.token;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.TokenFacade;
import kr.hhplus.be.server.application.UserCriteria;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenCommand;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.presentation.token.object.GenerateTokenRequest;
import kr.hhplus.be.server.presentation.token.object.TokenRequest;
import kr.hhplus.be.server.presentation.token.object.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class TokenController implements TokenApi {
    private final TokenFacade tokenFacade;
    private final TokenService tokenService;

    @PostMapping("/getToken")
    public ResponseEntity.BodyBuilder getToken(@Valid @RequestBody GenerateTokenRequest request) {
        tokenFacade.createToken(new UserCriteria(request.phoneNumber(), request.concertId()));
        return ResponseEntity.ok();
    }

    @PostMapping("/status")
    public ResponseEntity<TokenResponse> getStatus(@RequestBody TokenRequest tokenRequest) {
        Token token = tokenService.getToken(TokenCommand.builder().userId(tokenRequest.userId()).build());
        return new ResponseEntity<>(TokenResponse.from(token), HttpStatus.OK);
    }

    @Scheduled(fixedDelay = 3000)
    public void maintainActiveQueue() {
        tokenFacade.manageToken();
    }
}
