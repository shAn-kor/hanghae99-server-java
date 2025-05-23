package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UUID getUserId(UserCommand command) {
        User user = userRepository.getUser(command.phoneNumber());
        return user.getUserId();
    }
}
