package com.double_o.dambap.security.application;

import static com.double_o.dambap.auth.AuthConstants.ACCESS_TOKEN_HEADER_KEY;
import static com.double_o.dambap.auth.AuthConstants.REFRESH_TOKEN_HEADER_KEY;

import com.double_o.dambap.auth.model.AuthUser;
import com.double_o.dambap.auth.model.AuthUserImpl;
import com.double_o.dambap.redis.service.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${spring.security.jwt.secretKey}")
    private String SECRET_KEY;

    @Value("${spring.security.jwt.access.expiration}")
    private int accessTokenExpiration;

    @Value("${spring.security.jwt.refresh.expiration}")
    private int refreshTokenExpiration;

    @Getter
    private Long accessTokenExpirationInMillis;

    @Getter
    private Long refreshTokenExpirationInMillis;


    @PostConstruct
    public void init() {
        // 초를 밀리초로 변환
        this.accessTokenExpirationInMillis = this.accessTokenExpiration * 1000L;
        this.refreshTokenExpirationInMillis = this.refreshTokenExpiration * 1000L;
    }

    private SecretKey key;
    private final RedisService<String> redisService;  // RedisService 주입

    @Autowired
    public JwtService(@Value("${spring.security.jwt.secretKey}") String secretKey,
            RedisService<String> redisService) {
        this.redisService = redisService;
        this.SECRET_KEY = secretKey;

        // key 초기화 및 로그 확인
        if (SECRET_KEY != null) {
            key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        }
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .clockSkewSeconds(360) // 1분의 시간 차이를 허용
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .clockSkewSeconds(60) // 1분의 시간 차이를 허용
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token has expired: {}", e.getMessage());
            throw e; // 예외를 던져 JwtFilter에서 처리하도록 함
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.warn("Invalid signature for JWT token: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("General JWT processing error: {}", e.getMessage());
            throw e;
        }
    }

    // AccessToken 생성
    public String generateAccessToken(Long userId, String email) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationInMillis))
                .signWith(key)
                .compact();
    }

    // RefreshToken 생성
    public String generateRefreshToken(Long userId, String email) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationInMillis))
                .signWith(key)
                .compact();
    }

    // AccessToken에서 사용자 정보를 추출하는 메서드
    public AuthUser getAuthUserFromToken(String accessToken) {
        Claims claims = getClaimsFromToken(accessToken);

        Long userId = claims.get("userId", Long.class);
        String email = claims.getSubject();

        return new AuthUserImpl(userId, email, "username_placeholder");
    }

    // 로그아웃 시 AccessToken과 RefreshToken 제거 메서드
    public void remove(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractTokenFromRequest(request);

        if (refreshToken != null) {
            String email = getEmailFromToken(refreshToken);

            if (email != null && redisService.contains(email)) {
                redisService.delete(email);  // Redis에서 RefreshToken 삭제
            }

            // RefreshToken 쿠키에서 제거
            Cookie removeCookie = new Cookie(REFRESH_TOKEN_HEADER_KEY, null);
            removeCookie.setMaxAge(0);
            removeCookie.setHttpOnly(true);
            removeCookie.setSecure(true);
            response.addCookie(removeCookie);
        }

        // AccessToken 헤더에서 제거
        response.setHeader(ACCESS_TOKEN_HEADER_KEY, "");
    }

    // HttpServletRequest에서 RefreshToken을 추출하는 유틸리티 메서드
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 헤더에서 RefreshToken 추출 시도
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER_KEY);

        // 쿠키에서도 RefreshToken을 확인
        if (refreshToken == null || refreshToken.isBlank()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (REFRESH_TOKEN_HEADER_KEY.equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }
        }
        return refreshToken;
    }

    // RefreshToken에서 이메일을 추출하는 메서드
    public String getEmailFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (JwtException e) {
            log.warn("Failed to extract email from token", e);
            return null;
        }
    }

    public boolean isRefreshTokenStored(String token) {
        String key = "refresh_token:" + token;
        return redisService.exists(key); // Redis에 토큰 키가 존재하는지 확인
    }

}