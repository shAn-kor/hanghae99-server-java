package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.exception.InsufficientBalanceException;
import kr.hhplus.be.server.infrastructure.repository.PointRepository;
import kr.hhplus.be.server.infrastructure.repository.TokenRepository;
import kr.hhplus.be.server.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Testcontainers
class TokenFacadeIntegrationTest {
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application")
            .withInitScript("test-init.sql"); // 리소스에 위치한 SQL 스크립트 자동 실행

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private TokenFacade tokenFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private TokenRepository tokenRepository;

    private final String phoneNumber = "010-1234-5678";
    private UUID userId;

    @BeforeEach
    void setUp() {
        // 유저 등록
        User user = User.builder()
                .userId(UUID.randomUUID())
                .phoneNumber(phoneNumber)
                .build();
        userId = user.getUserId();
        userRepository.save(user);

        // 포인트 등록 (충분한 금액)
        Point point = Point.builder()
                .userId(userId)
                .balance(10_000L)
                .build();
        pointRepository.save(point);
    }

    @Test
    @DisplayName("토큰 생성 성공")
    void createToken_성공() throws Exception {
        // given
        UserCriteria criteria = new UserCriteria(phoneNumber);

        // when
        TokenResult result = tokenFacade.createToken(criteria);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.position()).isGreaterThanOrEqualTo(0);
        assertThat(result.valid()).isFalse(); // 초기값
        assertThat(result.createdAt()).isBeforeOrEqualTo(LocalDateTime.now());

        // 실제 DB에도 저장되었는지 확인
        assertThat(tokenRepository.getToken(userId)).isNotNull();
    }

    @Test
    @DisplayName("포인트 부족 시 예외 발생")
    void createToken_실패_잔액부족() {
        // given: 포인트 초기화
        String phoneNumber = "010-1234-1234";
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .userId(userId)
                .phoneNumber(phoneNumber)
                .build();
        userRepository.save(user);
        pointRepository.save(
                Point.builder()
                        .userId(userId)
                        .balance(0L)
                        .build()
        );
        UserCriteria criteria = new UserCriteria(phoneNumber);

        // expect
        assertThatThrownBy(() -> tokenFacade.createToken(criteria))
                .isInstanceOf(InsufficientBalanceException.class);
    }
}