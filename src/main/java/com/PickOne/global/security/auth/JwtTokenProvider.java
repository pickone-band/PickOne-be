package com.PickOne.global.security.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtTokenRepository jwtTokenRepository;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration.access}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenValidityInMilliseconds;

    private Key key;

    /**
     * 🔹 SecretKey 초기화 (Base64 디코딩 적용)
     */
    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String userId) {
        String token = createToken(userId, accessTokenValidityInMilliseconds);
        LocalDateTime expiresAt = Instant.now()
                .plusMillis(accessTokenValidityInMilliseconds)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // ✅ `jwtTokenService.saveToken()` 대신 `jwtTokenRepository` 직접 사용
        JwtTokenEntity tokenEntity = JwtTokenEntity.builder()
                .userId(userId)
                .accessToken(token)
                .refreshToken("")
                .expiresAt(expiresAt)
                .build();
        jwtTokenRepository.save(tokenEntity);

        return token;
    }

    public String createRefreshToken(String userId) {
        String token = createToken(userId, refreshTokenValidityInMilliseconds);
        LocalDateTime expiresAt = Instant.now()
                .plusMillis(refreshTokenValidityInMilliseconds)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // ✅ `jwtTokenService.saveToken()` 대신 `jwtTokenRepository` 직접 사용
        JwtTokenEntity tokenEntity = JwtTokenEntity.builder()
                .userId(userId)
                .accessToken(token)
                .refreshToken("")
                .expiresAt(expiresAt)
                .build();
        jwtTokenRepository.save(tokenEntity);

        return token;
    }

    /**
     * 🔹 JWT 생성 (공통 메서드)
     */
    private String createToken(String loginId, long validityInMilliseconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 🔹 JWT에서 사용자 ID (loginId) 가져오기
     */
    public String getUserIdFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            log.warn("⚠️ JWT Token 만료됨: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            log.warn("⚠️ JWT Token 유효하지 않음: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 🔹 JWT 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("⚠️ JWT Token 만료됨: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("⚠️ 지원하지 않는 JWT Token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("⚠️ 잘못된 JWT Token: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("⚠️ JWT 서명 검증 실패: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ JWT Token이 비어 있음: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 🔹 JWT로부터 Authentication 객체 생성
     */
    public Authentication getAuthentication(String token) {
        String loginId = getUserIdFromToken(token);
        if (loginId == null) {
            return null;
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * 🔹 HTTP 요청에서 JWT 토큰 추출
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

}
