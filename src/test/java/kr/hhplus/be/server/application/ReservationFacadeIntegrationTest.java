package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.dto.ReservationCriteria;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import kr.hhplus.be.server.infrastructure.repository.SeatRepository;
import kr.hhplus.be.server.infrastructure.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Testcontainers
@ActiveProfiles("test")
class ReservationFacadeIntegrationTest {
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
    private ReservationFacade reservationFacade;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        // 유효한 토큰 저장 (position <= 50)
        Token token = Token.builder()
                .userId(userId)
                .position(10)
                .valid(true)
                .createdAt(LocalDateTime.now())
                .build();
        tokenRepository.save(token);

        // 예약 가능한 좌석 세팅
        for (int i = 1; i <= 5; i++) {
            Seat seat = Seat.builder()
                    .venueId(1L)
                    .seatNumber(i)
                    .build();
            seatRepository.save(seat);
        }
    }

    @Test
    @DisplayName("유효한 토큰이 있을 경우 예약이 정상 동작한다")
    void reserveSeats_정상동작() throws Exception {
        ReservationCriteria criteria = ReservationCriteria.builder()
                .uuid(userId)
                .concertScheduleId(1L)
                .seatList(List.of(1L, 2L))
                .build();

        reservationFacade.reserveSeats(criteria);

        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        assertThat(reservations).isNotEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 토큰일 경우 예외 발생")
    void reserveSeats_대기열미통과_예외() {
        UUID invalidUser = UUID.randomUUID();
        ReservationCriteria criteria = ReservationCriteria.builder()
                .uuid(invalidUser)
                .concertScheduleId(1L)
                .seatList(List.of(1L, 2L))
                .build();

        assertThatThrownBy(() -> reservationFacade.reserveSeats(criteria))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("대기열을 통과하지 못했습니다");
    }
}