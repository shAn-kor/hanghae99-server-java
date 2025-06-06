package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.application.ReservationResult;
import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class ReservationServiceIntegrationTest {
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

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    @Transactional
    void reserve_정상동작() {
        // given
        List<ReservationItemCommand> itemCommands = new ArrayList<>();
        itemCommands.add(new ReservationItemCommand( 10L));
        itemCommands.add(new ReservationItemCommand( 11L));

        ReservationCommand command = new ReservationCommand(
                null,
                userId,
                1L,
                ReservationStatus.WAITING,
                LocalDateTime.now(),
                itemCommands
        );

        // when
        reservationService.reserve(command);

        // then
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        assertThat(reservations).isNotEmpty();
        Reservation saved = reservations.get(0);
        assertThat(reservationRepository.getItems(saved.getReservationId())).hasSize(2);
    }

    @Test
    void getTotalAmount_아이템수에따라금액계산() {
        // given
        Reservation reservation = reservationRepository.save(
                Reservation.builder().userId(userId).status(ReservationStatus.RESERVED).build()
        );
        reservationRepository.saveItem(new ReservationItem(reservation, 1L));
        reservationRepository.saveItem(new ReservationItem(reservation, 2L));

        ReservationIdCommand idCommand = new ReservationIdCommand(reservation.getReservationId());

        // when
        ReservationResult result = reservationService.getTotalAmount(idCommand);

        // then
        assertThat(result.totalAmount()).isEqualTo(2 * 500L);
    }
}