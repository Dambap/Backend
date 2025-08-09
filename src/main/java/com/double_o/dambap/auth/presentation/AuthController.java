package com.double_o.dambap.auth.presentation;

import static com.double_o.dambap.auth.AuthConstants.REFRESH_TOKEN_HEADER_KEY;

import com.double_o.dambap.auth.dto.request.AuthLoginRequest;
import com.double_o.dambap.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.double_o.dambap.auth.dto.response.AuthLoginResponse;

@Slf4j
@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    @Value("${spring.security.jwt.refresh.expiration}")
    private int refreshTokenExpiration;

    private final AuthService authService;

    private int refreshTokenExpirationMinutes;

    @PostConstruct
    public void init() {
        // 초를 분으로 변환
        this.refreshTokenExpirationMinutes = this.refreshTokenExpiration / 60;
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest request) {

        AuthLoginResponse response = authService.login(request);

        // RefreshToken을 HttpOnly 쿠키에 추가
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN_HEADER_KEY,
                        response.getRefreshToken())
                .httpOnly(true).secure(true).path("/").maxAge(refreshTokenExpirationMinutes)
                .build();

        // 헤더와 쿠키를 설정할 HttpHeaders 객체 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // AccessToken, RefreshToken 클라이언트로 반환
        return ResponseEntity.ok().headers(headers).body(response);
    }

    @Operation(summary = "RefreshToken 을 사용한 AccessToken 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {

        // 요청에서 쿠키로 RefreshToken 가져오기
        String refreshToken = extractTokenFromCookie(request);
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token is missing");
        }

        String newAccessToken = authService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(newAccessToken);
    }

    @Operation(summary = "로그아웃 요청")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        // 쿠키에서 RefreshToken 가져오기
        String refreshToken = extractTokenFromCookie(request);
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token is missing");
        }

        authService.logout(refreshToken);

        ResponseCookie expiredCookie = ResponseCookie.from(REFRESH_TOKEN_HEADER_KEY, "")
                .httpOnly(true).secure(true).path("/").maxAge(0).build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, expiredCookie.toString());

        return ResponseEntity.ok().headers(headers).body("Logged out successfully");
    }


    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_TOKEN_HEADER_KEY.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

}