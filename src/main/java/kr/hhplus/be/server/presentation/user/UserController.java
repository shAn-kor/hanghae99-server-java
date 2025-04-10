package kr.hhplus.be.server.presentation.user;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.presentation.user.object.UserRequest;
import kr.hhplus.be.server.presentation.user.object.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController implements UserApi {
    private final UserService userService;

    @Override
    @PostMapping("/uuid")
    public ResponseEntity<UserResponse> getUuid(@Valid @RequestBody UserRequest user) {
        UUID userId = userService.getUserId(UserCommand.builder().phoneNumber(user.phoneNumber()).build());
        return new ResponseEntity<>(UserResponse.builder().uuid(userId).build(), HttpStatus.OK);
    }
}
