package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.infrastructure.jpa.JpaPaymentRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Testcontainers
class PaymentServiceIntegrationTest {
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
    private PaymentService paymentService;

    @Autowired
    private JpaPaymentRepository jpaPaymentRepository;

    @Test
    @DisplayName("pay()는 결제 정보를 저장한다")
    void payTest() {
        // given
        Long reservationId = 1L;
        Long amount = 10000L;

        PaymentCommand command = new PaymentCommand(reservationId, amount);

        // when
        paymentService.pay(command);

        // then
        Payment saved = jpaPaymentRepository.findAll().stream()
                .filter(p -> p.getReservationId().equals(reservationId) && p.getAmount() == amount)
                .findFirst()
                .orElse(null);

        assertThat(saved).isNotNull();
        assertThat(saved.getReservationId()).isEqualTo(reservationId);
        assertThat(saved.getAmount()).isEqualTo(amount);
        assertThat(saved.getPaidAt()).isNotNull();
    }
}