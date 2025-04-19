package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface JpaTokenRepository extends JpaRepository<Token, Long> {
    @Query("select max(t.position) from token t")
    Integer findMaxPosition();

    Token findByUserId(UUID userId);
}
