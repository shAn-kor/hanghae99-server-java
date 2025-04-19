package kr.hhplus.be.server.domain.concertschedule;

import kr.hhplus.be.server.domain.concert.ConcertCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class ConcertScheduleServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application")
            .withInitScript("test-init.sql"); // src/test/resources 아래 있어야 함

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    ConcertScheduleService concertScheduleService;

    @Test
    @DisplayName("공연 ID로 공연 일정을 조회한다")
    void getConcertDatesTest() {
        // given
        ConcertCommand command = ConcertCommand.builder().concertId(1L).build();

        // when
        List<ConcertSchedule> result = concertScheduleService.getConcertDates(command);

        // then
        assertThat(result).hasSize(2);
    }
}