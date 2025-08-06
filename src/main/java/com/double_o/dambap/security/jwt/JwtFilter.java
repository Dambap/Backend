package com.double_o.dambap.security.jwt;


import static com.double_o.dambap.auth.AuthConstants.ACCESS_TOKEN_HEADER_KEY;

import com.double_o.dambap.auth.model.AuthUser;
import com.double_o.dambap.auth.model.AuthUserImpl;
import com.double_o.dambap.security.application.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {

        log.info("JwtFilter 실행됨");

        // 필터를 거치지 않도록 처리
        String requestURI = request.getRequestURI();

        log.info("requestURI: {}", requestURI);

        if (requestURI.equals("/") || requestURI.equals("/api/users/all") || requestURI.equals(
                "/api/auth/login") || requestURI.equals("/api/main/**")) {
            filterChain.doFilter(request, response);
            return;
        }

        // AccessToken 헤더에서 가져오기
        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER_KEY);

        if (accessToken != null) {
            try {
                // 토큰이 유효할 경우에만 사용자 정보를 추출
                Claims claims = jwtService.getClaimsFromToken(accessToken);
                log.info("JWT Claims: {}", claims);

                Long userId = claims.get("userId", Long.class);
                String email = claims.getSubject();

                AuthUser authUser = new AuthUserImpl(userId, email, null);
                log.info("AuthUser 생성됨: {}", authUser);

                // Spring Security 인증 객체 생성
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(authUser, null,
                                ((UserDetails) authUser).getAuthorities());

                // SecurityContext에 등록
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                request.setAttribute("authUser", authUser);  // AuthUser 자동 주입을 위한 코드
                request.setAttribute("userEmail", email);     // 사용자 이메일을 위한 코드

            } catch (ExpiredJwtException e) {
                // 만료된 토큰 예외 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("AccessToken is expired");
                return; // 요청 필터링 중단
            } catch (JwtException e) {
                // 유효하지 않은 토큰 예외 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid AccessToken");
                return; // 요청 필터링 중단
            } catch (Exception e) {
                // 기타 예외 처리
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter()
                        .write("An unexpected error occurred while validating the token");
                return; // 요청 필터링 중단
            }
        }

        // 토큰이 없거나 예외가 발생하지 않은 경우 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}