package kr.hhplus.be.server.domain.pointhistory;

import kr.hhplus.be.server.infrastructure.repository.PointHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class PointHistoryServiceIntegrationTest {
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
    private PointHistoryService pointHistoryService;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    private Long pointId;

    @BeforeEach
    void setUp() {
        // 사전 데이터 준비 (포인트 ID가 필요함)
        pointId = 1L; // 미리 준비된 포인트 ID 또는 테스트용 데이터
    }

    @Test
    @DisplayName("포인트 이력을 저장하고 조회할 수 있다")
    void saveAndGetPointHistory() {
        // given
        PointHistoryCommand command = new PointHistoryCommand(pointId, PointHistoryType.CHARGE);

        // when
        pointHistoryService.savePointHistory(command);
        List<PointHistory> result = pointHistoryService.getPointHistoryByPointId(command);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getPointId()).isEqualTo(pointId);
        assertThat(result.get(0).getType()).isEqualTo(PointHistoryType.CHARGE);
    }
}