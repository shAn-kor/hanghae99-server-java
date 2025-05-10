package kr.hhplus.be.server.domain.concert;

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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class ConcertServicePerformanceTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application")
            .withInitScript("test-init.sql");

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.0")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void setDatasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @DynamicPropertySource
    static void setRedisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Autowired
    private ConcertService concertService;

    private static final int REPEAT_COUNT = 100;

    @Test
    void 캐시_적용_전후_공연목록_조회_성능_비교() {

        // 1. 캐시 미적용 (최초 DB 접근)
        StopWatch sw1 = new StopWatch("Without Cache");
        sw1.start();
        for (int i = 0; i < REPEAT_COUNT; i++) {
            concertService.concertList(); // DB 접근
        }
        sw1.stop();

        // 2. 캐시 적용 상태 (두 번째 호출부터 Redis 접근)
        StopWatch sw2 = new StopWatch("With Cache");
        sw2.start();
        for (int i = 0; i < REPEAT_COUNT; i++) {
            concertService.concertList(); // Redis 접근
        }
        sw2.stop();

        System.out.println(sw1.prettyPrint());
        System.out.println(sw2.prettyPrint());

        assertThat(sw2.getTotalTimeMillis()).isLessThan(sw1.getTotalTimeMillis());
    }
}