package me.dodo.readingnotes.controller;

import jakarta.validation.Valid;
import me.dodo.readingnotes.dto.*;
import me.dodo.readingnotes.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Controller("/auth")
public class AuthController {

    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 일반 로그인
    @PostMapping("/login")
    public AuthResponse loginUser(@RequestBody @Valid LoginRequest request){
        log.debug("로그인 요청(request): {}", request.toString());

        AuthResult result = authService.loginUser(request.getEmail(), request.getPassword());

        return new AuthResponse("로그인 성공", new UserResponse(result.getUser()), result.getAccessToken(), result.getRefreshToken());
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public AuthResponse reissueUser(@RequestBody @Valid ReissueRequest request){
        log.debug("토큰 재발급 요청: {}", request.toString());

        AuthResult result = authService.reissueAccessToken(request.getRefreshToken(), request.getDeviceInfo());

        return new AuthResponse("재발급 성공", new UserResponse(result.getUser()), result.getAccessToken(), result.getRefreshToken());
    }
}
