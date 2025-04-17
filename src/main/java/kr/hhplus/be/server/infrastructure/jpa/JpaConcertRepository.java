package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.concert.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaConcertRepository extends JpaRepository<Concert, Long> {
}
