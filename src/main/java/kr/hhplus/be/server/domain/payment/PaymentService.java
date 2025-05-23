package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.infrastructure.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment pay(PaymentCommand command) {
        Payment payment = Payment.builder().reservationId(command.reservationId()).amount(command.amount()).build();
        paymentRepository.savePayment(payment);
        return payment;
    }
}
