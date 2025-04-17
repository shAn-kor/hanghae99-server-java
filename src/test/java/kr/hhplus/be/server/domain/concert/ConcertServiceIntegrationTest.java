package kr.hhplus.be.server.domain.concert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class ConcertServiceIntegrationTest {
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
    private ConcertService concertService;

    @Test
    @DisplayName("concertList()는 모든 공연 목록을 반환한다")
    void concertListTest() {
        // when
        List<Concert> result = concertService.concertList();

        // then
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        assertThat(result)
                .extracting(Concert::getConcertName)
                .contains("봄콘서트", "여름페스티벌");
    }
}