package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application")
            .withInitScript("test-init.sql");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    private final String phoneNumber = "010-1234-5678";

//    @BeforeEach
//    void setUp() {
//        // 사용자 저장
//        User user = User.builder()
//                .userId(UUID.randomUUID())
//                .phoneNumber(phoneNumber)
//                .build();
//
//        userRepository.save(user);
//    }

    @Test
    @DisplayName("전화번호로 사용자 조회 후 UUID 반환")
    @Transactional
    void getUserId_정상동작() {
        // given
        UserCommand command = new UserCommand(phoneNumber);

        // when
        UUID userId = userService.getUserId(command);

        // then
        assertThat(userId).isNotNull();
        User user = userRepository.getUser(phoneNumber);
        assertThat(userId).isEqualTo(user.getUserId());
    }
}