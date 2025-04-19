package kr.hhplus.be.server.domain.Venue;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.concert.ConcertCommand;
import kr.hhplus.be.server.infrastructure.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class VenueServiceIntegrationTest {
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
    private VenueService venueService;

    @Autowired
    private VenueRepository venueRepository;

    private Venue savedVenue;

    @BeforeEach
    void setUp() {
        // 테스트용 Venue 저장
        savedVenue = Venue.builder()
                .venueName("올림픽홀")
                .address("서울 송파구 올림픽로")
                .seatCount(3)
                .build();

        savedVenue = venueRepository.save(savedVenue);
    }

    @Test
    @DisplayName("getConcertVenue - 정상 동작")
    @Transactional
    void getConcertVenue_정상() {
        // given
        ConcertCommand command = new ConcertCommand(null, 1L);

        // when
        Venue result = venueService.getConcertVenue(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getVenueId()).isEqualTo(savedVenue.getVenueId());
        assertThat(result.getVenueName()).isEqualTo("올림픽홀");
    }
}