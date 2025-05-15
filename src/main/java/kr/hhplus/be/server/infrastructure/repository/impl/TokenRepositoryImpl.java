package kr.hhplus.be.server.infrastructure.repository.impl;

import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.infrastructure.jpa.JpaTokenRepository;
import kr.hhplus.be.server.infrastructure.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {
    private final JpaTokenRepository repository;

    @Override
    public Token getToken(UUID userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public Token save(Token token) {
        return repository.save(token);
    }

    @Override
    public Integer getMaxPosition() {
        return repository.findMaxPosition();
    }

    @Override
    public void fillActiveQueue(UUID uuid, Long aLong) {

    }

    @Override
    public void endActiveToken(UUID uuid, Long aLong) {

    }

    @Override
    public Token getToken(UUID uuid, Long aLong) {
        return null;
    }

    @Override
    public Token generateToken(UUID uuid, Long concertId) {
        return null;
    }

    @Override
    public void endToken(UUID uuid, Long aLong) {

    }
}
