package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.ReservationIdCommand;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.token.TokenCommand;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.infrastructure.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final PaymentService paymentService;
    private final ReservationService reservationService;
    private final PointService pointService;
    private final TokenService tokenService;

    @DistributedLock(prefix = "payment", key = "#criteria.userId()", waitTime = 0)
    public void paySeat(PaymentCriteria criteria) {
        try {
            reservationService.checkStatus(ReservationIdCommand.builder().reservationId(criteria.reservationId()).build());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        ReservationResult result = reservationService.getTotalAmount(ReservationIdCommand.builder().reservationId(criteria.reservationId()).build());

        PointCommand pointCommand = PointCommand.builder().userId(criteria.userId()).point(result.totalAmount()).build();
        pointService.checkPoint(pointCommand);

        pointService.usePoint(pointCommand);

        tokenService.endActiveToken(TokenCommand.builder().userId(criteria.userId()).build());

        PaymentCommand paymentCommand = PaymentCommand.builder()
                .reservationId(criteria.reservationId())
                .amount(result.totalAmount()).build();
        paymentService.pay(paymentCommand);

        reservationService.endReserve(ReservationIdCommand.builder().reservationId(criteria.reservationId()).build());
    }
}
