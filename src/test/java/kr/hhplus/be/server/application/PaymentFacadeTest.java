package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.concert.ConcertRankingService;
import kr.hhplus.be.server.domain.concertschedule.ConcertScheduleService;
import kr.hhplus.be.server.domain.payment.*;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.ReservationIdCommand;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.token.TokenService;
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
    private TokenService tokenService;
    private PaymentFacade paymentFacade;
    private ConcertScheduleService concertScheduleService;
    private ConcertRankingService concertRankingService;
    private PaymentEventPublisher paymentEventPublisher;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        reservationService = mock(ReservationService.class);
        pointService = mock(PointService.class);
        tokenService = mock(TokenService.class);
        concertScheduleService = mock(ConcertScheduleService.class);
        concertRankingService = mock(ConcertRankingService.class);
        paymentEventPublisher = mock(PaymentEventPublisher.class);

        paymentFacade = new PaymentFacade(
                paymentService,
                reservationService,
                pointService,
                tokenService,
                concertScheduleService,
                concertRankingService,
                paymentEventPublisher
        );
    }

    @Test
    @DisplayName("paySeat()는 잔액이 충분할 경우 포인트를 사용하고 결제를 수행하고 이벤트를 발행한다")
    void paySeat_success() {
        // given
        UUID userId = UUID.randomUUID();
        Long reservationId = 1L;
        Long pointId = 1L;
        Long totalAmount = 1000L;
        Long paymentId = 10L;

        PaymentCriteria criteria = new PaymentCriteria(userId, reservationId);
        ReservationResult mockResult = new ReservationResult(totalAmount);
        Point mockPoint = Point.builder().userId(userId).balance(totalAmount).build();
        Payment mockPayment = Payment.builder().reservationId(reservationId).amount(totalAmount).build();

        when(reservationService.getTotalAmount(new ReservationIdCommand(reservationId)))
                .thenReturn(mockResult);
        when(pointService.getPointByUserId(PointCommand.builder().userId(userId).build()))
                .thenReturn(mockPoint);
        when(paymentService.pay(new PaymentCommand(reservationId, totalAmount)))
                .thenReturn(mockPayment);

        // when
        paymentFacade.paySeat(criteria);

        // then
        PointCommand expectedCommand = new PointCommand(userId, pointId, totalAmount);

        verify(pointService).getPointByUserId(PointCommand.builder().userId(userId).build());
        verify(pointService).checkPoint(expectedCommand);
        verify(pointService).usePoint(expectedCommand);
        verify(paymentService).pay(new PaymentCommand(reservationId, totalAmount));
        verify(paymentEventPublisher).success(
                PaymentCompletedEvent.builder()
                        .paymentId(paymentId)
                        .reservationId(reservationId)
                        .build()
        );
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