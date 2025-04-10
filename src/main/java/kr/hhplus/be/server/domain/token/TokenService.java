package kr.hhplus.be.server.domain.token;

import kr.hhplus.be.server.infrastructure.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public Token generateToken(TokenCommand command) {
        return tokenRepository.generateToken(command.userId());
    }

    public Token getToken(TokenCommand tokenCommand) {
        return tokenRepository.getToken(tokenCommand.userId());
    }

    @Scheduled(fixedRate = 10000)
    public void updateTokenValidity() {
        List<Token> tokens = tokenRepository.findAll();
        for (Token token : tokens) {
            if (token.position() <= 50) {
                tokenRepository.updateValid(token.tokenId(), true);
            }
        }
    }

    public boolean isValid(TokenCommand command) {
        return tokenRepository.checkValid(command.userId());
    }
}
