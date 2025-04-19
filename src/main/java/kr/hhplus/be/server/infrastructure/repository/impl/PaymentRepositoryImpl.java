package kr.hhplus.be.server.infrastructure.repository.impl;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.infrastructure.jpa.JpaPaymentRepository;
import kr.hhplus.be.server.infrastructure.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final JpaPaymentRepository jpaPaymentRepository;

    @Override
    public void savePayment(Payment payment) {
        jpaPaymentRepository.save(payment);
    }

    @Override
    public Payment findByReservationId(Long reservationId) {

        return jpaPaymentRepository.findByReservationId(reservationId);
    }
}
