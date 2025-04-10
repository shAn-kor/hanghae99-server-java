package kr.hhplus.be.server.presentation.payment;

import kr.hhplus.be.server.application.PaymentFacade;
import kr.hhplus.be.server.presentation.payment.object.PaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaymentControllerUnitTest {

    private PaymentFacade paymentFacade;
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        paymentFacade = mock(PaymentFacade.class);
        paymentController = new PaymentController(paymentFacade);
    }

    @Test
    @DisplayName("pay()는 PaymentFacade의 paySeat를 호출한다")
    void pay_success() {
        // given
        UUID userId = UUID.randomUUID();
        PaymentRequest request = new PaymentRequest(1L);

        // when
        paymentController.pay(request);

        // then
        verify(paymentFacade).paySeat(request.toCriteria());
    }
}