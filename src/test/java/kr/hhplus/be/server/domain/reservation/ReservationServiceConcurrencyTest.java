package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import kr.hhplus.be.server.infrastructure.repository.ReservationRepository;
import kr.hhplus.be.server.infrastructure.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReservationServiceConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    private static final Long CONCERT_SCHEDULE_ID = 1L;
    private static final Integer SEAT_NUMBER = 1;

    private Long seatId;

    @BeforeEach
    void setUp() {
        Seat seat = Seat.builder()
                .concertScheduleId(CONCERT_SCHEDULE_ID)
                .seatNumber(SEAT_NUMBER)
                .status(SeatStatus.EMPTY)
                .build();
        seatRepository.save(seat);
        this.seatId = seat.getSeatId(); // 나중에 사용
    }

    @Test
    void 동시에_동일한_좌석_예약시_단_한명만_성공() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<Boolean>> results = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            UUID userId = UUID.randomUUID(); // 각각 다른 사용자

            results.add(executorService.submit(() -> {
                try {
                    ReservationItemCommand item = ReservationItemCommand.builder()
                            .seatId(seatId)
                            .build();

                    ReservationCommand command = ReservationCommand.builder()
                            .userId(userId)
                            .status(ReservationStatus.WAITING)
                            .createdAt(LocalDateTime.now())
                            .items(List.of(item))
                            .build();

                    reservationService.reserve(command);
                    return true;
                } catch (Exception e) {
                    return false;
                } finally {
                    latch.countDown();
                }
            }));
        }

        latch.await();

        long successCount = results.stream().filter(f -> {
            try {
                return f.get();
            } catch (Exception e) {
                return false;
            }
        }).count();

        // 좌석 하나이므로 단 하나의 예약만 성공해야 함
        assertThat(successCount).isEqualTo(1);
    }
}