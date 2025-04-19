package kr.hhplus.be.server.infrastructure.repository.impl;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.jpa.JpaUserRepository;
import kr.hhplus.be.server.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;

    @Override
    @Transactional
    public User getUser(String phoneNumber) {
        User user = jpaUserRepository.findByPhoneNumber(phoneNumber);

        if (user == null) {
            user = User.builder().phoneNumber(phoneNumber).build();
            jpaUserRepository.save(user);
        }
        return user;
    }

    @Override
    public void save(User user) {
        jpaUserRepository.save(user);
    }
}
