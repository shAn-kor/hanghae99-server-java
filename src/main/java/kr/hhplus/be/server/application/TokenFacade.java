package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertService;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.token.*;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.exception.InsufficientBalanceException;
import kr.hhplus.be.server.presentation.KafkaPublishingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenFacade {
    private final PointService pointService;
    private final UserService userService;
    private final TokenService tokenService;
    private final ConcertService concertService;
    private final TokenEventPublisher tokenEventPublisher;

    @Transactional
    public void createToken(UserCriteria criteria) throws InsufficientBalanceException, KafkaPublishingException {
        UUID userId = userService.getUserId(
                UserCommand.builder().phoneNumber(criteria.phoneNumber()).build()
        );

        pointService.checkPoint(PointCommand.builder().point(0L).userId(userId).build());

        tokenEventPublisher.publish(TokenEvent.builder().userId(userId).concertId(criteria.concertId()).build());
    }

    @Transactional
    public void manageToken() {
        List<Long> concertIds = concertService.concertList().stream().map(Concert::getConcertId).toList();

        for (Long concertId : concertIds) {
            tokenService.fillActiveQueue(TokenCommand.builder().concertId(concertId).build());
        }
    }

}
