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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class PaymentFacadeTest {

    private PaymentService paymentService;
    private ReservationService reservationService;
    private PointService pointService;

    private PaymentFacade paymentFacade;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        reservationService = mock(ReservationService.class);
        pointService = mock(PointService.class);

        paymentFacade = new PaymentFacade(paymentService, reservationService, pointService);
    }

    @Test
    @DisplayName("paySeat()는 잔액이 충분할 경우 포인트를 사용하고 결제를 수행한다")
    void paySeat_success() {
        // given
        UUID userId = UUID.randomUUID();
        Long reservationId = 1L;
        Long totalAmount = 1000L;

        PaymentCriteria criteria = new PaymentCriteria(userId, reservationId);
        ReservationResult mockResult = new ReservationResult(totalAmount);

        when(reservationService.getTotalAmount(new ReservationIdCommand(reservationId)))
                .thenReturn(mockResult);

        // when
        paymentFacade.paySeat(criteria);

        // then
        verify(pointService).checkPoint(new PointCommand(userId, totalAmount));
        verify(pointService).usePoint(new PointCommand(userId, totalAmount));
        verify(paymentService).pay(new PaymentCommand(reservationId, totalAmount));
    }

    @Test
    @DisplayName("paySeat()는 잔액이 부족하면 예외를 던진다")
    void paySeat_insufficientBalance() {
        // given
        UUID userId = UUID.randomUUID();
        Long reservationId = 1L;
        Long totalAmount = 2000L;

        PaymentCriteria criteria = new PaymentCriteria(userId, reservationId);
        ReservationResult mockResult = new ReservationResult(totalAmount);

        when(reservationService.getTotalAmount(new ReservationIdCommand(reservationId)))
                .thenReturn(mockResult);

        // when & then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> paymentFacade.paySeat(criteria))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("잔액이 부족합니다.");

        verify(pointService, never()).usePoint(any());
        verify(paymentService, never()).pay(any());
    }
}