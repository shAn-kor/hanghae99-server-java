package kr.hhplus.be.server.domain.token;

import kr.hhplus.be.server.infrastructure.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
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
        List<Token> tokens = tokenRepository.findValidTokens();
        for (Token token : tokens) {
            token.updateValidTrue();
            tokenRepository.save(token);
        }
    }

    public void isValid(TokenCommand command) throws AccessDeniedException {
        Token token = tokenRepository.getToken(command.userId());
        if (token == null) {
            throw new AccessDeniedException("대기열을 통과하지 못했습니다.");
        }
    }

    public void endToken(TokenCommand tokenCommand) {
        Token token = tokenRepository.getToken(tokenCommand.userId());
        token.setPositionZero();
        tokenRepository.save(token);
    }
}
