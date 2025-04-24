package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.infrastructure.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SeatServiceConcurrencyTest {

    @Autowired
    private SeatService seatService;

    @Autowired
    private SeatRepository seatRepository;

    private static final Long VENUE_ID = 1L;
    private static final Integer SEAT_NUMBER = 1;

    @BeforeEach
    void setUp() {
        // 테스트 좌석 초기화
        Seat seat = Seat.builder()
                .venueId(VENUE_ID)
                .seatNumber(SEAT_NUMBER)
                .build();

        seatRepository.save(seat);
    }

    @Test
    void 동시에_같은_좌석_예약시_중복_예약_발생하지_않음() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<Future<Boolean>> results = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            results.add(executorService.submit(() -> {
                try {
                    SeatCommand command = SeatCommand.builder()
                            .venueId(VENUE_ID)
                            .seatNumbers(List.of(1L))
                            .build();

                    seatService.reserveSeat(command);
                    return true; // 예약 성공
                } catch (Exception e) {
                    return false; // 예약 실패
                } finally {
                    latch.countDown();
                }
            }));
        }

        latch.await();

        long successCount = results.stream().filter(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                return false;
            }
        }).count();

        // 한 명만 성공해야 함
        assertThat(successCount).isEqualTo(1);
    }
}