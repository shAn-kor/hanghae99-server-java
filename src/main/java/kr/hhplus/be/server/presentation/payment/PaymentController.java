package kr.hhplus.be.server.presentation.payment;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.PaymentFacade;
import kr.hhplus.be.server.presentation.payment.object.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.BodyBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController implements PaymentApi {
    private final PaymentFacade paymentFacade;

    @PostMapping("/pay")
    public BodyBuilder pay(@Valid @RequestBody PaymentRequest request) {
        paymentFacade.paySeat(request.toCriteria());
        return ResponseEntity.ok();
    }
}
