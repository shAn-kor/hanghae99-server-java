package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.infrastructure.repository.SeatRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Testcontainers
class SeatServiceIntegrationTest {
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
    private SeatService seatService;

    @Autowired
    private SeatRepository seatRepository;

    @BeforeEach
    void setUp() {
        // 테스트를 위한 기본 좌석 1~5번 추가
        for (int i = 1; i <= 5; i++) {
            Seat seat = Seat.builder()
                    .concertScheduleId(1L)
                    .seatNumber(i)
                    .status(SeatStatus.EMPTY)
                    .build();
            seatRepository.save(seat);
        }
    }

    @Test
    void getEmptySeats_빈좌석조회() {
        // when
        List<Seat> emptySeats = seatService.getEmptySeats();

        // then
        assertThat(emptySeats).hasSize(5);
        assertThat(emptySeats).allMatch(seat -> seat.getStatus().equals(SeatStatus.EMPTY));
    }

    @Test
    void reserveSeat_좌석예약() {
        // given
        SeatCommand command = SeatCommand.builder().concertScheduleId(1L).seatNumbers(List.of(1, 2)).build();

        // when
        List<Seat> reservedSeats = seatService.reserveSeat(command);

        // then
        assertThat(reservedSeats).hasSize(2);
        assertThat(reservedSeats).allMatch(seat -> seat.getStatus().equals(SeatStatus.RESERVED));
    }

    @Test
    void unReserveSeat_좌석예약취소() {
        // given
        SeatCommand command = new SeatCommand(1L, List.of(1));
        List<Seat> reserved = seatService.reserveSeat(command);
        Long reservedSeatId = reserved.get(0).getSeatId();

        // when
        seatService.unReserveSeat(new SeatIdCommand(reservedSeatId));

        // then
        Seat unreserved = seatRepository.findById(reservedSeatId);
        assertThat(unreserved.getStatus()).isEqualTo(SeatStatus.EMPTY);
    }
}