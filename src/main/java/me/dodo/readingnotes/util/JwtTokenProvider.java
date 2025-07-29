package me.dodo.readingnotes.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    // 나중에 application.properties로 옮겨야 함.
    // 시크릿 키 HS256은 대칭형 알고리즘임.
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 토큰 유효 시간
    // 30분
    private final long accessTokenValidity = 1000 * 60 * 30;
    // 7일
    private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 7;

    // Acess Token 생성
    public String createAccessToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .setSubject(email) // 토큰 주인 정보
                .setIssuedAt(now) // 발급 시간
                .setExpiration(expiryDate) // 만료 시간
                .signWith(key) // 서명(변조 방지)
                .compact(); // 최종 문자열로 변환
    }

    // Refresh Token 생성
    public String createRefreshToken() {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidity);
        // access token 새로 받을 때만 사용하므로 이메일 담지 않음.
        // DB에 저장해서 나중에 비교할 때 사용함.
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }
    
    // 클라이언트에서 보낸 토큰에서 email 추출
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // 예외 없이 파싱되면 유효함.
                return true;
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다: {}",e.getMessage());
        } catch (JwtException e) {
            log.error("유효하지 않은 토큰입니다: {}",e.getMessage());
        }
        return false;
    }

    // 토큰 만료 시간 확인
    public Date getExpirationDate(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
