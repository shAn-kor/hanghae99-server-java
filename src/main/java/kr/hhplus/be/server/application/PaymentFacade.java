package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.concert.ConcertCommand;
import kr.hhplus.be.server.domain.concert.ConcertRankingService;
import kr.hhplus.be.server.domain.concertschedule.ConcertSchedule;
import kr.hhplus.be.server.domain.concertschedule.ConcertScheduleCommand;
import kr.hhplus.be.server.domain.concertschedule.ConcertScheduleService;
import kr.hhplus.be.server.domain.payment.*;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationIdCommand;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationTotalCommand;
import kr.hhplus.be.server.domain.token.TokenCommand;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.infrastructure.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final PaymentService paymentService;
    private final ReservationService reservationService;
    private final PointService pointService;
    private final TokenService tokenService;
    private final ConcertScheduleService concertScheduleService;
    private final ConcertRankingService concertRankingService;
    private final PaymentEventPublisher paymentEventPublisher;

    @DistributedLock(prefix = "payment", key = "#criteria.reservationId()", waitTime = 0)
    public void paySeat(PaymentCriteria criteria) {
        try {
            reservationService.checkStatus(ReservationIdCommand.builder().reservationId(criteria.reservationId()).build());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        ReservationResult result = reservationService.getTotalAmount(ReservationIdCommand.builder().reservationId(criteria.reservationId()).build());

        Point point = pointService.getPointByUserId(PointCommand.builder().userId(criteria.userId()).build());
        PointCommand pointCommand = PointCommand.builder().userId(criteria.userId()).point(result.totalAmount()).pointId(point.getPointId()).build();
        pointService.checkPoint(pointCommand);
        pointService.usePoint(pointCommand);

        tokenService.endActiveToken(TokenCommand.builder().userId(criteria.userId()).build());

        PaymentCommand paymentCommand = PaymentCommand.builder()
                .reservationId(criteria.reservationId())
                .amount(result.totalAmount()).build();
        Payment payment = paymentService.pay(paymentCommand);

        reservationService.endReserve(ReservationIdCommand.builder().reservationId(criteria.reservationId()).build());

        PaymentCompletedEvent event = PaymentCompletedEvent.builder().paymentId(payment.getPaymentId()).reservationId(payment.getReservationId()).build();
        paymentEventPublisher.success(event);

        // ✅ 트랜잭션 커밋 이후 매진 여부 확인 및 Redis 랭킹 등록
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                Reservation reservation = reservationService.getReservation(ReservationIdCommand.builder().reservationId(criteria.reservationId()).build());
                ConcertSchedule schedule = concertScheduleService.getConcertSchedule(ConcertScheduleCommand.builder().concertScheduleId(reservation.getConcertScheduleId()).build());
                Integer getTotalConcertTicketCount = concertScheduleService.getTotalTicketCount(ConcertScheduleCommand.builder().concertId(schedule.getConcertId()).build());
                List<Long> concertScheduleIdList = concertScheduleService.getConcertScheduleIdList(ConcertScheduleCommand.builder().concertId(schedule.getConcertId()).build());

                if (reservationService.checkSoldOut(ReservationTotalCommand.builder().concertScheduleIdList(concertScheduleIdList).totalTicketCount(getTotalConcertTicketCount).build())) {
                    concertRankingService.registerSoldOutConcert(ConcertCommand.builder().concertId(schedule.getConcertId()).build());
                }
            }
        });
    }
}
