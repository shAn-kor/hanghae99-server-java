package kr.hhplus.be.server.domain.token;

import kr.hhplus.be.server.infrastructure.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@Transactional
@Testcontainers
@ActiveProfiles("test")
class TokenServiceStressTest {
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

    @Autowired
    TokenService tokenService;

    @Autowired
    TokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
        // given: 1000개의 토큰을 초기 저장 (position: 1~1000)
        for (int i = 1; i <= 1000; i++) {
            Token token = Token.builder()
                    .userId(UUID.randomUUID())
                    .position(i)
                    .valid(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            tokenRepository.save(token);
        }
    }
}
