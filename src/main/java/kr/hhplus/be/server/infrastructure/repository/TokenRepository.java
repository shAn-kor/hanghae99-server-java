package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.token.Token;

import java.util.UUID;

public interface TokenRepository {
    Token getToken(UUID uuid);

    Token save(Token token);

    Integer getMaxPosition();

    void fillActiveQueue(UUID uuid, Long aLong);

    void endActiveToken(UUID uuid, Long aLong);

    Token getToken(UUID uuid, Long aLong);

    Token generateToken(UUID uuid, Long concertId);

    void endToken(UUID uuid, Long aLong);
}
