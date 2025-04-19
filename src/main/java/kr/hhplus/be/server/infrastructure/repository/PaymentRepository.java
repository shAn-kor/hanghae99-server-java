package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.payment.Payment;

public interface PaymentRepository {
    void savePayment(Payment payment);

    Payment findByReservationId(Long reservationId);
}
