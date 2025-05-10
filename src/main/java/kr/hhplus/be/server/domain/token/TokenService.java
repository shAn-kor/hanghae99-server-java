package kr.hhplus.be.server.domain.token;

import java.nio.file.AccessDeniedException;

public interface TokenService {
    Token generateToken(TokenCommand command);

    Token getToken(TokenCommand tokenCommand);

    void isValid(TokenCommand command) throws AccessDeniedException;

    void endToken(TokenCommand tokenCommand);

    void fillActiveQueue(TokenCommand command);

    void endActiveToken(TokenCommand command);
}
