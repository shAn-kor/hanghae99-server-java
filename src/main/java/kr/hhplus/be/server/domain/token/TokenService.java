package kr.hhplus.be.server.domain.token;

import kr.hhplus.be.server.infrastructure.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public Token generateToken(TokenCommand command) {
        return tokenRepository.generateToken(command.userId(), command.concertId());
    }

    public Token getToken(TokenCommand tokenCommand) {
        return tokenRepository.getToken(tokenCommand.userId(), tokenCommand.concertId());
    }

    public void isValid(TokenCommand command) throws AccessDeniedException {
        Token token = tokenRepository.getToken(command.userId());
        if (token == null) {
            throw new AccessDeniedException("대기열을 통과하지 못했습니다.");
        }
    }

    public void endToken(TokenCommand tokenCommand) {
        tokenRepository.endToken(tokenCommand.userId(), tokenCommand.concertId());
    }

    public void fillActiveQueue(TokenCommand command) {
        tokenRepository.fillActiveQueue(command.userId(), command.concertId());
    }

    public void endActiveToken(TokenCommand command) {
        tokenRepository.endActiveToken(command.userId(), command.concertId());
    }
}
