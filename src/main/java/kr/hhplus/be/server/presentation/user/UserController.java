package kr.hhplus.be.server.presentation.user;

import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.user.object.UserRequest;
import kr.hhplus.be.server.presentation.user.object.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping("/uuid")
    public ResponseEntity<UserResponse> getUuid(@Valid @RequestBody UserRequest user) {
        return new ResponseEntity<>(new UserResponse(UUID.randomUUID()), HttpStatus.OK);
    }
}
