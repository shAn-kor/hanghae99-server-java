package kr.hhplus.be.server.presentation.token;

import kr.hhplus.be.server.application.TokenFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class TokenScheduler {
    private final TokenFacade tokenFacade;

    @Scheduled(fixedDelay = 3000)
    public void maintainActiveQueue() {
        tokenFacade.manageToken();
    }
}
