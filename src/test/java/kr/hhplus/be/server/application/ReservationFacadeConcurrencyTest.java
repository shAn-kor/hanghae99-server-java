package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.dto.ReservationCriteria;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import kr.hhplus.be.server.infrastructure.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Transactional
public class ReservationFacadeConcurrencyTest {
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
    private ReservationFacade reservationFacade;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    private final int THREAD_COUNT = 10;
    private UUID testUserId;
    private Long testConcertScheduleId;
    private Long testSeatId;
    private Long venueId;

    @BeforeEach
    void setUp() {
        // 예약 테스트용 좌석 1개 생성
        testUserId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        testConcertScheduleId = 1L;
        venueId = 1L;
        testSeatId = 100L;
    }

    @Test
    void 동시에_같은_좌석_예약_요청시_단_한_명만_성공해야_한다() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    ReservationCriteria criteria = ReservationCriteria.builder()
                            .uuid(testUserId) // 사용자 UUID는 다르게 (테스트 확장 시)
                            .venueId(venueId)
                            .seatList(List.of(testSeatId)) // 같은 좌석
                            .concertScheduleId(testConcertScheduleId)
                            .build();

                    reservationFacade.reserveSeats(criteria);
                } catch (Exception e) {
                    System.out.println("[Thread-" + threadNum + "] 예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        List<Reservation> reservations = reservationRepository.findByUserIdAndConcertScheduleId(testUserId, testConcertScheduleId);
        long reservedCount = reservations.stream()
                .flatMap(r -> r.getReservationItems().stream())
                .filter(item -> item.getId().getSeatId().equals(testSeatId))
                .count();

        assertThat(reservedCount).isEqualTo(1);
    }
}
