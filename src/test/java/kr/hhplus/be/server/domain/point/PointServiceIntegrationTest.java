package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.exception.InsufficientBalanceException;
import kr.hhplus.be.server.infrastructure.repository.PointRepository;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Testcontainers
class PointServiceIntegrationTest {
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
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        Point point = Point.builder()
                .userId(userId)
                .balance(10000L)
                .build();
        pointRepository.save(point);
    }

    @Test
    @DisplayName("잔액 조회 테스트")
    void getPointByUserIdTest() {
        PointCommand command = new PointCommand(userId, 1L,100L);

        Point result = pointService.getPointByUserId(command);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBalance()).isEqualTo(10000L);
    }

    @Test
    @DisplayName("포인트 충전 성공 테스트")
    void chargePointTest() {
        PointCommand command = new PointCommand(userId, 1L,100L);

        pointService.chargePoint(command);

        Point updated = pointService.getPointByUserId(command);
        assertThat(updated.getBalance()).isEqualTo(15000L);
    }

    @Test
    @DisplayName("포인트 사용 성공 테스트")
    void usePointTest() {
        PointCommand command = new PointCommand(userId, 1L,100L);

        pointService.usePoint(command);

        Point updated = pointService.getPointByUserId(command);
        assertThat(updated.getBalance()).isEqualTo(6000L);
    }

    @Test
    @DisplayName("포인트 부족 시 예외 발생 테스트")
    void insufficientBalanceTest() {
        PointCommand command = new PointCommand(userId, 1L,100L);

        assertThrows(InsufficientBalanceException.class, () -> {
            pointService.checkPoint(command);
        });
    }
}