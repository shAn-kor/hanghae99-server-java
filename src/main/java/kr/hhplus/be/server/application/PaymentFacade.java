package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.ReservationIdCommand;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.token.TokenCommand;
import kr.hhplus.be.server.domain.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final PaymentService paymentService;
    private final ReservationService reservationService;
    private final PointService pointService;
    private final TokenService tokenService;

    public void paySeat(PaymentCriteria criteria) {
        ReservationResult result = reservationService.getTotalAmount(new ReservationIdCommand(criteria.reservationId()));

        PointCommand pointCommand = PointCommand.builder().userId(criteria.userId()).point(result.totalAmount()).build();
        pointService.checkPoint(pointCommand);

        pointService.usePoint(pointCommand);

        tokenService.endToken(TokenCommand.builder().userId(criteria.userId()).build());

        PaymentCommand paymentCommand = PaymentCommand.builder()
                .reservationId(criteria.reservationId())
                .amount(result.totalAmount()).build();
        paymentService.pay(paymentCommand);
    }
}
