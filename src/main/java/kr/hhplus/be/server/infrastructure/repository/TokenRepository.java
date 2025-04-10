package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.token.Token;

import java.util.List;
import java.util.UUID;

public interface TokenRepository {
    Token generateToken(UUID userId);

    Token getToken(UUID uuid);

    List<Token> findAll();

    void updateValid(UUID uuid, boolean b);

    boolean checkValid(UUID uuid);
}
