package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.infrastructure.repository.PointHistoryRepository;
import kr.hhplus.be.server.infrastructure.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class PointFacadeIntegrationTest {
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
    private PointFacade pointFacade;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        Point point = Point.builder()
                .userId(testUserId)
                .balance(0L)
                .build();

        pointRepository.save(point);
    }

    @Test
    void getPoint_조회_및_히스토리저장() {
        // when
        PointResult result = pointFacade.getPoint(new PointCriteria(testUserId, 0L));

        // then
        assertThat(result.uuid()).isEqualTo(testUserId);
        assertThat(result.point()).isEqualTo(0L);

        List<PointHistoryResult> history = pointFacade.getPointHistory(new PointCriteria(testUserId, 0L));
        assertThat(history).hasSize(1);
        assertThat(history.get(0).type()).isEqualTo(kr.hhplus.be.server.domain.pointhistory.PointHistoryType.CHECK);
    }

    @Test
    void chargePoint_충전후_잔액변경과_히스토리저장() {
        // given
        long chargeAmount = 5000L;

        // when
        pointFacade.chargePoint(new PointCriteria(testUserId, chargeAmount));

        // then
        Point point = pointRepository.getPoint(testUserId);
        assertThat(point.getBalance()).isEqualTo(chargeAmount);

        List<PointHistoryResult> history = pointFacade.getPointHistory(new PointCriteria(testUserId, 0L));
        assertThat(history).extracting("type").contains(kr.hhplus.be.server.domain.pointhistory.PointHistoryType.CHARGE);
    }

    @Test
    void usePoint_사용후_잔액감소와_히스토리저장() {
        // given
        long initial = 10000L;
        Point point = pointRepository.getPoint(testUserId);
        point.charge(initial);
        pointRepository.save(point);

        // when
        pointFacade.usePoint(new PointCriteria(testUserId, 3000L));

        // then
        Point result = pointRepository.getPoint(testUserId);
        assertThat(result.getBalance()).isEqualTo(7000L);

        List<PointHistoryResult> history = pointFacade.getPointHistory(new PointCriteria(testUserId, 0L));
        assertThat(history).extracting("type").contains(kr.hhplus.be.server.domain.pointhistory.PointHistoryType.USE);
    }
}

