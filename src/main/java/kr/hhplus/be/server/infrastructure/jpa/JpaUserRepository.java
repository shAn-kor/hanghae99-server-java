package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    User findByPhoneNumber(String phone);
}
