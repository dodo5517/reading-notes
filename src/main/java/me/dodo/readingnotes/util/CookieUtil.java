package me.dodo.readingnotes.util;

import org.springframework.http.ResponseCookie;

public class CookieUtil {
    public static ResponseCookie createRefreshTokenCookie(String refreshToken, boolean isSecure) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(isSecure)  // 운영 시에는 true로 해야 함
                .path("/") // 모든 경로에 쿠키 전송
                .maxAge(7 * 24 * 60 * 60) // 7일
                .sameSite("Strict") // CSRF 방지
                .build();
    }

    public static ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
