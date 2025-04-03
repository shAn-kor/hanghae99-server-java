package kr.hhplus.be.server.presentation.token;

import kr.hhplus.be.server.presentation.token.object.TokenRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/token")
public class TokenController {
    @GetMapping("/getToken")
    public ResponseEntity<String> getToken(@RequestParam UUID uuid) {
        return ResponseEntity.ok("token");
    }

    @PostMapping("/status")
    public ResponseEntity<Integer> getStatus(@RequestBody TokenRequest tokenRequest) {
        return new ResponseEntity<>(0, HttpStatus.OK);
    }
}
