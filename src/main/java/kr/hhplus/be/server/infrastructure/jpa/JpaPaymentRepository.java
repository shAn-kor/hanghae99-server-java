package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByReservationId(Long reservationId);
}
