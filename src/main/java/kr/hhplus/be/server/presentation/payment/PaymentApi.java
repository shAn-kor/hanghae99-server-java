package kr.hhplus.be.server.presentation.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.payment.object.PaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "💳 결제 API", description = "포인트를 사용한 결제 처리 API")
@RequestMapping("/payment")
public interface PaymentApi {

    @Operation(summary = "결제 요청", description = "예약된 좌석에 대해 포인트를 차감하여 결제합니다.")
    @PostMapping("/pay")
    ResponseEntity.BodyBuilder pay(@Valid @RequestBody PaymentRequest request);
}