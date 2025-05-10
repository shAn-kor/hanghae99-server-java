package kr.hhplus.be.server.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class PaymentFacadeConcurrencyTest {
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application")
            .withInitScript("test-init.sql"); // 리소스에 위치한 SQL 스크립트 자동 실행

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.0")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        String host = redisContainer.getHost();
        Integer port = redisContainer.getMappedPort(6379);
        registry.add("spring.redis.host", () -> host);
        registry.add("spring.redis.port", () -> port);
    }

    @Autowired
    private PaymentFacade paymentFacade;

    private final int THREAD_COUNT = 10;

    @Test
    void 동시에_결제요청을_보내면_한_요청만_성공한다() throws InterruptedException {
        // given
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Long reservationId = 1L;

        PaymentCriteria criteria = new PaymentCriteria(userId, reservationId);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        List<String> results = Collections.synchronizedList(new ArrayList<>());

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    paymentFacade.paySeat(criteria);
                    results.add("success");
                } catch (Exception e) {
                    results.add("fail");
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // then
        long successCount = results.stream().filter(r -> r.equals("success")).count();
        long failCount = results.stream().filter(r -> r.equals("fail")).count();

        System.out.println("성공: " + successCount + ", 실패: " + failCount);

        assertThat(successCount).isEqualTo(1); // 하나만 성공해야 함
        assertThat(failCount).isEqualTo(THREAD_COUNT - 1);
    }
}