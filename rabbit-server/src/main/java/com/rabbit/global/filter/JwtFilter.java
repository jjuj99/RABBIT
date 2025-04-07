package com.rabbit.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 인증 없이 통과 가능한 경로
    private static final String[] WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/api/v1/auth/nonce",
            "/api/v1/auth/login",
            "/api/v1/auth/sign-up",
            "/api/v1/auth/refresh",
            "/api/v1/auth/check-nickname",
            "/api/v1/bank/**",
            "/api/v1/sse/**",
            "/api/v1/auctions",
            "/api/v1/ipfs/**",
            "/api/v1/loans/**"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 화이트리스트 경로는 필터 통과
        String requestURI = request.getRequestURI();

        if (isWhitelisted(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 헤더 확인
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization header: {}" + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, ErrorCode.JWT_REQUIRED);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String userId = jwtUtil.getUserIdFromToken(token);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, List.of(() -> "DUMMY"));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            sendErrorResponse(response, ErrorCode.JWT_EXPIRED);
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException e) {
            // 유효하지 않은 토큰 (구조 오류, 서명 오류 등)
            sendErrorResponse(response, ErrorCode.JWT_INVALID);
        }
    }

    private boolean isWhitelisted(String uri) {
        for (String pattern : WHITELIST) {
            if (pathMatcher.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        CustomApiResponse<?> errorResponse = CustomApiResponse.error(errorCode.getStatus().value(), errorCode.getDefaultMessage());

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}
