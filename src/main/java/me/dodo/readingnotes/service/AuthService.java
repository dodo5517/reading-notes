package me.dodo.readingnotes.service;

import me.dodo.readingnotes.domain.RefreshToken;
import me.dodo.readingnotes.domain.User;
import me.dodo.readingnotes.dto.AuthResult;
import me.dodo.readingnotes.repository.RefreshTokenRepository;
import me.dodo.readingnotes.repository.UserRepository;
import me.dodo.readingnotes.util.DeviceInfoParser;
import me.dodo.readingnotes.util.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;

@Service
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService(JwtTokenProvider jwtTokenProvider,
                       RefreshTokenRepository refreshTokenRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 유저 로그인(필요한 최소 정보만 따로 받음)
    @Transactional // 트랜젝션 처리
    public AuthResult loginUser(String email, String password, String userAgent) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if(user.getIsDeleted()){
            throw new IllegalArgumentException("탈퇴한 계정입니다.");
        }
        if(!passwordEncoder.matches(password,user.getPassword())){ // 평문 비교가 아닌 해시 비교
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        log.info("로그인 성공");

        // 디바이스 정보 파싱
        String deviceInfo = DeviceInfoParser.extractDeviceInfo(userAgent);
        log.debug("deviceInfo: {}", deviceInfo);

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // 실서비스에서는 토큰 로그는 절대 기록하지 않는 게 원칙. 남기더라도 debug 레벨 + redaction 시스템 필요
        log.info("accessToken: {}", accessToken.substring(0,4));

        // 기존 동일 기기 토큰 삭제
        refreshTokenRepository.deleteByUserIdAndDeviceInfo(user.getId(), deviceInfo);

        // refresh 토큰 만료 시간
        Date refreshExpiry = jwtTokenProvider.getExpirationDate(refreshToken);

        // refresh 토큰 저장
        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setUser(user);
        tokenEntity.setToken(refreshToken);
        // Date 타입을 LocalDateTime으로 변환
        tokenEntity.setExpiryDate(refreshExpiry.toInstant()
                .atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime());
        tokenEntity.setDeviceInfo(deviceInfo);
        
        refreshTokenRepository.save(tokenEntity); // DB에 저장

        // 실서비스에서는 토큰 로그는 절대 기록하지 않는 게 원칙. 남기더라도 debug 레벨 + redaction 시스템 필요
        log.info("refreshToken: {}", tokenEntity.getToken().substring(0,4));

        return new AuthResult(user, accessToken, refreshToken);
    }

    // 토큰 재발급
    @Transactional
    public AuthResult reissueAccessToken(String refreshToken, String userAgent) {
        // 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 refresh_token 입니다.");
        }

        // DB에서 토큰 조회
        RefreshToken tokenInDb = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 refresh_token 입니다."));

        // 디바이스 정보 파싱
        String deviceInfo = DeviceInfoParser.extractDeviceInfo(userAgent);
        log.debug("deviceInfo: {}", deviceInfo);
        
        // DB에서 device_info 검증
        if (!tokenInDb.getDeviceInfo().equals(deviceInfo)) {
            throw new IllegalArgumentException("기기 정보가 일치하지 않습니다.");
        }

        User user = tokenInDb.getUser();
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail());

        // 필요 시 refreshToken 재발급
        return new AuthResult(user, newAccessToken, refreshToken);
    }
}
