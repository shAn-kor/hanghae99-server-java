package kr.hhplus.be.server.domain.concertschedule;

import kr.hhplus.be.server.domain.concert.ConcertCommand;
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
public class ConcertScheduleServicePerformanceTest {

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
    static void redisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Autowired
    private ConcertScheduleService concertScheduleService;

    private static final Long CONCERT_ID = 1L;
    private static final int REPEAT_COUNT = 100;

    @Test
    void 캐시_적용_전후_성능_차이_비교() {
        ConcertCommand command = ConcertCommand.builder().concertId(CONCERT_ID).build();

        // 1. 캐시 미적용 (최초 DB 접근)
        StopWatch sw1 = new StopWatch("Without Cache");
        sw1.start();
        for (int i = 0; i < REPEAT_COUNT; i++) {
            concertScheduleService.getConcertDates(command); // DB 호출
        }
        sw1.stop();

        // 2. 캐시 적용 (두 번째부터 Redis 접근)
        StopWatch sw2 = new StopWatch("With Cache");
        sw2.start();
        for (int i = 0; i < REPEAT_COUNT; i++) {
            concertScheduleService.getConcertDates(command); // Redis 호출
        }
        sw2.stop();

        System.out.println(sw1.prettyPrint());
        System.out.println(sw2.prettyPrint());

        assertThat(sw2.getTotalTimeMillis()).isLessThan(sw1.getTotalTimeMillis());
    }
}