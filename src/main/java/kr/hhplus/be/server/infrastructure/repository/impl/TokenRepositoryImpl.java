package kr.hhplus.be.server.infrastructure.repository.impl;

import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.infrastructure.jpa.JpaTokenRepository;
import kr.hhplus.be.server.infrastructure.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public List<Token> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Token> findValidTokens() {
        return repository.findValidTokens();
    }

    @Override
    public Token save(Token token) {
        return repository.save(token);
    }

    @Override
    public Integer getMaxPosition() {
        return repository.findMaxPosition();
    }
}
