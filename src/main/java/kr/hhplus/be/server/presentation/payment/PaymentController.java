package kr.hhplus.be.server.presentation.payment;

import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.payment.object.PaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @PostMapping("/pay")
    public BodyBuilder point(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok();
    }
}
