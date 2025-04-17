package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.token.Token;

import java.util.List;
import java.util.UUID;

public interface TokenRepository {
    Token getToken(UUID uuid);

    List<Token> findAll();

    List<Token> findValidTokens();

    Token save(Token token);

    Integer getMaxPosition();
}
