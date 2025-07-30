package me.dodo.readingnotes.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import me.dodo.readingnotes.dto.*;
import me.dodo.readingnotes.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 일반 로그인
    @PostMapping("/login")
    public AuthResponse loginUser(@RequestBody @Valid LoginRequest request,
                                  HttpServletRequest httpRequest,
                                  HttpServletResponse httpResponse) {
        log.debug("로그인 요청(request): {}", request.toString());

        // Header에서 User-Agent 가져옴
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResult result = authService.loginUser(
                request.getEmail(),
                request.getPassword(),
                userAgent
        );

        // refreshToken -> HttpOnly 쿠키에 저장
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(false) // 실제 운영할 때는 true로 해야함. HTTPS는 기본으로 해야함
                .path("/") // 모든 경로에 쿠키 전송
                .maxAge(7 * 24 * 60 * 60) // 7일
                .sameSite("Strict") // CSRF 방지
                .build();
        // 헤더에 저장
        httpResponse.addHeader("Set-Cookie", refreshCookie.toString());

        // accessToken -> JSON 응답 body에 포함
        // refreshToken은 쿠키로 보냈으므로 응답 body에는 null로 처리
        return new AuthResponse("로그인 성공", new UserResponse(result.getUser()), result.getAccessToken(), null);
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public AuthResponse reissueUser(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                    HttpServletRequest httpRequest){
        log.debug("토큰 재발급 요청");

        // refresh token 유효성 검사
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("refresh token이 쿠키에 존재하지 않습니다.");
        }

        // Header에서 User-Agent 가져옴
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResult result = authService.reissueAccessToken(refreshToken, userAgent);

        return new AuthResponse("토큰 재발급 성공", new UserResponse(result.getUser()), result.getAccessToken(), null);
    }
}
