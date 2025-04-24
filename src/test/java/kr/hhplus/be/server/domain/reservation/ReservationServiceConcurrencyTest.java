package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import org.junit.jupiter.api.Assertions;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Transactional
class ReservationServiceConcurrencyTest {
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
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    private final Long seatId = 100L;

    @Test
    void testConcurrentReserve_withRealDB() throws InterruptedException {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Long concertScheduleId = 1L;

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        ReservationCommand command = new ReservationCommand(
                userId,
                concertScheduleId,
                ReservationStatus.WAITING,
                LocalDateTime.now(),
                List.of(new ReservationItemCommand(seatId))
        );

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    reservationService.reserve(command);
                } catch (Exception e) {
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long reservationCount = reservationRepository.findByUserIdAndConcertScheduleId(userId, concertScheduleId).size();
        System.out.println("총 예약 수: " + reservationCount);

        Assertions.assertEquals(1, reservationCount, "동시성 테스트에서 중복 예약이 발생하지 않아야 합니다.");
    }
}