package com.rabbit.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    private Key key;

    private final long ACCESS_TOKEN_EXPIRE_MS = 1000L * 60 * 30; // 30분
    private final long REFRESH_TOKEN_EXPIRE_MS = 1000L * 60 * 60 * 24 * 7; // 7일

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secret);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_MS))
                .signWith((SecretKey) key, Jwts.SIG.HS256)
                .compact();
    }

    public String createRefreshToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_MS))
                .signWith((SecretKey) key, Jwts.SIG.HS256)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", String.class);
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("JWT 토큰 파싱 오류: {}", e.getMessage());
            throw e;
        }
    }
}
