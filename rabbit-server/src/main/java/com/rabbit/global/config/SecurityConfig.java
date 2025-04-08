package com.rabbit.global.config;

import com.rabbit.global.filter.JwtFilter;
import com.rabbit.global.security.JwtAuthenticationEntryPoint;
import com.rabbit.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 활성화
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 비활성화
                .authorizeHttpRequests(auth -> auth // API 접근 권한 설정
                                .requestMatchers(HttpMethod.GET, "/swagger-ui/**", "/v3/api-docs/**", "/favicon.ico", "/api/v1/auctions").permitAll()
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/api/v1/bank/**").permitAll()
                                .requestMatchers("/api/v1/auctions/**").permitAll()
                                .requestMatchers("/api/v1/sse/**").permitAll()
                                .requestMatchers("/api/v1/coins/**").permitAll()
                                .requestMatchers("/api/v1/ipfs/**").permitAll()
                                .requestMatchers("/api/v1/loans/**").permitAll()
                                .anyRequest().authenticated() // 모든 요청 인증 필요
//                                .anyRequest().permitAll()
                )
                .exceptionHandling(exception
                        -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)) // 인증 실패 시 예워 처리
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
                .build();
    }

    // 허용된 프론트엔드 도메인만 API 요청 가능
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("https://localhost:8080", "http://localhost:8080", "https://j12a604.p.ssafy.io", "http://localhost:5173", "http://127.0.0.1:5500")); // 허용할 도메인
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // 허용할 HTTP 메소드
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        configuration.setExposedHeaders(List.of("Authorization")); // 클라이언트가 Authorization 읽을 수 있게
        configuration.setAllowCredentials(true); // JWT 인증을 위한 쿠키 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 API 엔드포인트에 적용

        return source;
    }
}
