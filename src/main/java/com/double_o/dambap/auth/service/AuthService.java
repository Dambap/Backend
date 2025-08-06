package com.double_o.dambap.auth.service;

import com.double_o.dambap.auth.dto.request.AuthLoginRequest;
import com.double_o.dambap.auth.dto.response.AuthLoginResponse;
import com.double_o.dambap.exception.auth.AuthInvalidException;
import com.double_o.dambap.exception.dto.ErrorType;
import com.double_o.dambap.redis.service.RedisService;
import com.double_o.dambap.security.application.JwtService;
import com.double_o.dambap.user.application.UserService;
import com.double_o.dambap.user.domain.User;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final RedisService<String> redisService;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.jwt.refresh.expiration}")
    private int refreshTokenExpiration;

    public AuthLoginResponse login(AuthLoginRequest loginRequest) {

        // 이메일로 사용자 조회
        User user = userService.getByEmail(loginRequest.getEmail());

        // 비밀번호가 일치하는지 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthInvalidException(ErrorType.WRONG_PASSWORD_ERROR);
        }

        // 로그인 성공 시 AccessToken과 RefreshToken 발급
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        // RefreshToken 생성 및 Redis에 저장 (7일간 유효)
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());
        log.info("Redis에 저장할 RefreshToken: {}", refreshToken);

        // Redis에 RefreshToken 저장 및 로그 추가
        redisService.set(user.getEmail(), refreshToken, refreshTokenExpiration / 60);
        log.info("Redis에 RefreshToken 저장 완료: 키={}, 만료시간={}", user.getEmail(),
                refreshTokenExpiration / 60);

        return new AuthLoginResponse(accessToken, refreshToken);
    }

    public String refreshAccessToken(String refreshToken) {

        // RefreshToken 유효성 검증
        if (!jwtService.validateToken(refreshToken)) {
            throw new AuthInvalidException(ErrorType.INVALID_REFRESH_TOKEN_ERROR);
        }

        // RefreshToken 에서 사용자 이메일, id 추출
        String email = jwtService.getClaimsFromToken(refreshToken).getSubject();
        Long userId = jwtService.getClaimsFromToken(refreshToken).get("userId", Long.class);

        // Redis 에서 해당 이메일의 RefreshToken 조회 (유효성 재검증)
        String storedRefreshToken = redisService.getValue(email);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new AuthInvalidException(ErrorType.REFRESH_TOKEN_MISMATCH_ERROR);
        }

        // 새로운 AccessToken 생성 및 반환
        return jwtService.generateAccessToken(userId, email);
    }

    public void logout(String refreshToken) {

        try {
            String email = jwtService.getClaimsFromToken(refreshToken).getSubject();
            redisService.delete(email);
        } catch (ExpiredJwtException e) {
            throw new AuthInvalidException(ErrorType.EXPIRED_REFRESH_TOKEN_ERROR);
        }
    }
}