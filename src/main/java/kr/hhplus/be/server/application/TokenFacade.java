package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.dto.TokenResult;
import kr.hhplus.be.server.application.dto.UserCriteria;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenCommand;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.exception.InsufficientBalanceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenFacade {
    private final PointService pointService;
    private final UserService userService;
    private final TokenService tokenService;

    public TokenResult createToken(UserCriteria criteria) throws InsufficientBalanceException {
        UUID userId = userService.getUserId(
                UserCommand.builder().phoneNumber(criteria.phoneNumber()).build()
        );

        pointService.checkPoint(PointCommand.builder().userId(userId).build());

        Token token = tokenService.generateToken(TokenCommand.builder().userId(userId).build());

        return TokenResult.builder()
                .userId(userId)
                .position(token.position())
                .valid(token.valid())
                .createdAt(token.createdAt())
                .build();
    }

}
