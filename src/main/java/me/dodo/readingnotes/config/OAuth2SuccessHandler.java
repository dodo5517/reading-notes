package me.dodo.readingnotes.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.dodo.readingnotes.domain.RefreshToken;
import me.dodo.readingnotes.domain.User;
import me.dodo.readingnotes.repository.RefreshTokenRepository;
import me.dodo.readingnotes.repository.UserRepository;
import me.dodo.readingnotes.service.CustomOAuth2UserService;
import me.dodo.readingnotes.util.CookieUtil;
import me.dodo.readingnotes.util.DeviceInfoParser;
import me.dodo.readingnotes.util.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;

// Spring Security의 설정 흐름에 직접 참여하는 구성요소이기 때문에 config에 저장.
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    public OAuth2SuccessHandler(final JwtTokenProvider jwtTokenProvider,
                                final UserRepository userRepository,
                                final RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 이메일입니다."));

        // accessToken, refreshToken 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken();
        log.debug("refreshToken: {}", refreshToken);

        // Header에서 User-Agent 가져옴
        String userAgent = request.getHeader("User-Agent");
        // 디바이스 정보 파싱
        String deviceInfo = DeviceInfoParser.extractDeviceInfo(userAgent);
        log.debug("deviceInfo: {}", deviceInfo);

        RefreshToken tokenEntity = new RefreshToken();

        tokenEntity.setUser(user);
        tokenEntity.setToken(refreshToken);
        log.debug("entity_refreshToken: {}", tokenEntity.getToken());
        log.debug("user: {}", user.toString());

        Date refreshExpiry = jwtTokenProvider.getExpirationDate(refreshToken);
        // Date 타입을 LocalDateTime으로 변환(로그인 하는 시점에 지정하므로 공통부분임)
        tokenEntity.setExpiryDate(refreshExpiry.toInstant()
                .atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime());
        tokenEntity.setDeviceInfo(deviceInfo);

        // DB에 refreshToken 저장
        refreshTokenRepository.save(tokenEntity);

        // refreshToken → HttpOnly 쿠키로 저장
        ResponseCookie refreshCookie = CookieUtil.createRefreshTokenCookie(refreshToken, false);

        // 헤더에 저장
        response.addHeader("Set-Cookie", refreshCookie.toString());

        // 프론트로 accessToken과 함께 리디렉션
        String redirectUrl = "http://localhost:3000/oauth/callback?accessToken=" + accessToken;
        response.sendRedirect(redirectUrl);
    }
}
