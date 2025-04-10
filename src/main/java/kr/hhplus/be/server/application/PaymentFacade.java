package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.dto.PaymentCriteria;
import kr.hhplus.be.server.application.dto.ReservationResult;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.ReservationIdCommand;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.exception.InsufficientBalanceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final PaymentService paymentService;
    private final ReservationService reservationService;
    private final PointService pointService;

    public void paySeat(PaymentCriteria criteria) {
        ReservationResult result = reservationService.getTotalAmount(new ReservationIdCommand(criteria.reservationId()));

        PointCommand pointCommand = PointCommand.builder().userId(criteria.userId()).point(result.totalAmount()).build();
        Boolean isPayPossible = pointService.checkPoint(pointCommand);
        if (!isPayPossible) {
            throw new InsufficientBalanceException("잔액이 부족합니다.");
        }

        pointService.usePoint(pointCommand);

        PaymentCommand paymentCommand = PaymentCommand.builder()
                .reservationId(criteria.reservationId())
                .amount(result.totalAmount()).build();
        paymentService.pay(paymentCommand);
    }
}
