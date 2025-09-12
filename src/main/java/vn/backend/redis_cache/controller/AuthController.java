package vn.backend.redis_cache.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.backend.redis_cache.dto.request.LoginRequest;
import vn.backend.redis_cache.dto.response.AuthResponse;
import vn.backend.redis_cache.dto.response.ResponseData;
import vn.backend.redis_cache.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("User login server username={}", request.getUsername());
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(new ResponseData<>(
                HttpStatus.OK.value(),
                response
        ));
    }
}
