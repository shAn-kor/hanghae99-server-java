package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.infrastructure.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public void pay(PaymentCommand command) {
        Payment payment = Payment.builder().reservationId(command.reservationId()).amount(command.amount()).build();
        paymentRepository.savePayment(payment);
    }
}
