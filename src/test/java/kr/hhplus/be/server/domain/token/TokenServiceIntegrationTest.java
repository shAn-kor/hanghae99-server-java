package kr.hhplus.be.server.domain.token;

import kr.hhplus.be.server.infrastructure.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class TokenServiceIntegrationTest {
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
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void generateToken_토큰생성_정상작동() {
        // given
        TokenCommand command = new TokenCommand(userId);

        // when
        Token token = tokenService.generateToken(command);

        // then
        Token savedToken = tokenRepository.getToken(userId);
        assertThat(savedToken).isNotNull();
        assertThat(savedToken.getUserId()).isEqualTo(userId);
        assertThat(savedToken.getPosition()).isGreaterThanOrEqualTo(1);
        assertThat(savedToken.getValid()).isFalse();
    }

    @Test
    void updateTokenValidity_포지션_50이하인경우_validTrue() {
        // given
        for (int i = 0; i < 60; i++) {
            UUID uid = UUID.randomUUID();
            tokenService.generateToken(new TokenCommand(uid));
        }

        // when
        tokenService.updateTokenValidity();

        // then
        List<Token> tokens = tokenRepository.findAll();
        long validCount = tokens.stream().filter(Token::getValid).count();

        assertThat(validCount).isEqualTo(50);
    }

    @Test
    void isValid_정상작동() {
        // given
        Token token = tokenService.generateToken(new TokenCommand(userId));
        token.updateValidTrue();
        tokenRepository.save(token);

        // when
        boolean isValid = tokenService.isValid(new TokenCommand(userId));

        // then
        assertThat(isValid).isTrue();
    }
}