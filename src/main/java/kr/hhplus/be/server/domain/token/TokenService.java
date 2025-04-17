package kr.hhplus.be.server.domain.token;

import kr.hhplus.be.server.infrastructure.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public Token generateToken(TokenCommand command) {
        Integer maxPosition = tokenRepository.getMaxPosition();
        if (maxPosition == null) {
            maxPosition = 0;
        }

        Token token = Token.builder()
                .userId(command.userId())
                .position(maxPosition + 1)
                .valid(false)
                .createdAt(LocalDateTime.now())
                .build();
        return tokenRepository.save(token);
    }

    public Token getToken(TokenCommand tokenCommand) {
        return tokenRepository.getToken(tokenCommand.userId());
    }

    @Scheduled(fixedRate = 10000)
    public void updateTokenValidity() {
        List<Token> tokens = tokenRepository.findAll();
        for (Token token : tokens) {
            if (token.getPosition() <= 50) {
                token.updateValidTrue();
                tokenRepository.save(token);
            }
        }
    }

    public boolean isValid(TokenCommand command) {
        Token token = tokenRepository.getToken(command.userId());
        if (token == null) {
            return false;
        }
        return token.getValid();
    }
}
