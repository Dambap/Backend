package com.double_o.dambap.security.config;

import com.double_o.dambap.auth.service.CustomUserDetailsService;
import com.double_o.dambap.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;  // CustomUserDetailsService 주입
    private final JwtFilter jwtFilter;  // JWT 필터 주입

    @Value("${spring.security.jwt.secretKey}")
    private String secretKey;

    // AuthenticationManager 빈 등록
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // PasswordEncoder 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder(
                secretKey,
                16,
                310000,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
    }

    // DaoAuthenticationProvider 빈 등록 (CustomUserDetailsService와 PasswordEncoder 설정)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);  // CustomUserDetailsService 사용
        authProvider.setPasswordEncoder(passwordEncoder());  // PasswordEncoder 사용
        return authProvider;
    }

    // SecurityFilterChain 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(cnf -> cnf.ignoringRequestMatchers("/api/**"));

        // 접근 허용 관련 설정
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/", "/auth/login", "/register", "/users/all")
                    .permitAll(); // 홈, 로그인, 회원가입 페이지 접근 허용
            auth.requestMatchers("/api/**").permitAll(); // API 요청 전체 허용 (필요에 따라 수정 가능)
            auth.anyRequest().permitAll();
        });

        // 폼 로그인 설정
        http.formLogin(cnf -> {
            cnf.loginPage("/auth/login") // 로그인 페이지 경로 설정
                    .loginProcessingUrl("/login") // 로그인 폼의 action 경로와 일치
                    .defaultSuccessUrl("/") // 로그인 성공 시 리다이렉트할 페이지
                    .permitAll(); // 로그인 관련 URL은 누구나 접근 가능
        });

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가

        return http.build();
    }
}
