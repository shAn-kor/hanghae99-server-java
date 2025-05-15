package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationItem;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.infrastructure.repository.PaymentRepository;
import kr.hhplus.be.server.infrastructure.repository.PointRepository;
import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class PaymentFacadeIntegrationTest {
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

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private UUID testUserId;
    private Long testReservationId;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String REDIS_KEY = "concert:soldout:ranking";

    @BeforeEach
    void setUp() {
        // given - 테스트에 필요한 유저, 예약, 포인트 준비
        testUserId = UUID.randomUUID();

        Point point = Point.builder()
                .userId(testUserId)
                .balance(100_000L)
                .build();
        pointRepository.save(point);

        Reservation reservation = Reservation.builder()
                .userId(testUserId)
                .concertScheduleId(1L)
                .status(ReservationStatus.WAITING)
                .build();
        Reservation savedReservation = reservationRepository.save(reservation);

        reservationRepository.saveItem(new ReservationItem(savedReservation, 1L));
        reservationRepository.saveItem(new ReservationItem(savedReservation, 2L));

        redisTemplate.delete(REDIS_KEY); // 테스트마다 클린업
    }

    @Test
    void paySeat_정상결제() {
        // when
        PaymentCriteria criteria = new PaymentCriteria(testUserId, testReservationId);
        paymentFacade.paySeat(criteria);

        // then - 포인트가 차감되고 결제 내역이 생성되어야 함
        Point updatedPoint = pointRepository.getPoint(testUserId);
        assertThat(updatedPoint.getBalance()).isLessThan(100_000L);

        Payment payment = paymentRepository.findByReservationId(testReservationId);
        assertThat(payment).isNotNull();
        assertThat(payment.getAmount()).isEqualTo(2 * 500L); // 2좌석
    }

    @Test
    void paySeat_매진이면_트랜잭션커밋후_Redis랭킹등록된다() {
        // given
        UUID userId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        Long reservationId = 11L;
        Long concertId = 100L;

        PaymentCriteria criteria = new PaymentCriteria(userId, reservationId);

        // 테스트를 위해 실제 DB 또는 H2 등에서 reservation, schedule, seat 등 등록 필요
        // 또는 미리 저장된 데이터를 기반으로 진행

        // when
        paymentFacade.paySeat(criteria);

        // then
        Set<ZSetOperations.TypedTuple<String>> ranking = redisTemplate.opsForZSet().rangeWithScores(REDIS_KEY, 0, -1);
        assertThat(ranking).isNotEmpty();

        boolean containsConcert = ranking.stream()
                .anyMatch(tuple -> tuple.getValue().equals(concertId.toString()));

        assertThat(containsConcert).isTrue();
    }
}