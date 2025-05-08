package kr.hhplus.be.server.domain.point;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.StopWatch;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class PointServicePerformanceTest {
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

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.0")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Autowired
    private PointService pointService;

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final int REPEAT_COUNT = 100;

    @Test
    void 캐시_적용_전후_성능_차이_비교() {
        PointCommand command = new PointCommand(USER_ID, 0L);

        // 1. 캐시 초기화 (강제로 비움)
        pointService.chargePoint(PointCommand.builder()
                .userId(USER_ID)
                .point(0L)
                .build()); // point 변경 없이 캐시만 날리기

        // 2. 캐시 미적용 상태 측정 (최초 DB 접근)
        StopWatch sw1 = new StopWatch("Without Cache");
        sw1.start();
        for (int i = 0; i < REPEAT_COUNT; i++) {
            pointService.getPointByUserId(command); // DB 접근
        }
        sw1.stop();

        // 3. 캐시 적용 상태 측정 (두 번째 호출부터 캐시)
        StopWatch sw2 = new StopWatch("With Cache");
        sw2.start();
        for (int i = 0; i < REPEAT_COUNT; i++) {
            pointService.getPointByUserId(command); // Redis 캐시 접근
        }
        sw2.stop();

        // 결과 출력
        System.out.println(sw1.prettyPrint());
        System.out.println(sw2.prettyPrint());

        assertThat(sw2.getTotalTimeMillis()).isLessThan(sw1.getTotalTimeMillis());
    }
}