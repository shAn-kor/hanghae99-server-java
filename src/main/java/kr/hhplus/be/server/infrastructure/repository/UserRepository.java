package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.user.User;

public interface UserRepository {
    User getUser(String phoneNumber);

    void save(User user);
}
