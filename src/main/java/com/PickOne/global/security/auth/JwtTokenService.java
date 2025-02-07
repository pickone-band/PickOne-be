package com.PickOne.global.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtTokenRepository jwtTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // JWT 저장
    @Transactional
    public void saveToken(String userId, String accessToken, String refreshToken, LocalDateTime expiresAt) {

        jwtTokenRepository.deleteByUserId(userId);
        JwtTokenEntity tokenEntity = JwtTokenEntity.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .build();
        jwtTokenRepository.save(tokenEntity);
    }

    // 액세스 토큰 조회
    public Optional<JwtTokenEntity> getTokenByAccessToken(String accessToken) {
        return jwtTokenRepository.findByAccessToken(accessToken);
    }

    // 리프레시 토큰 조회
    public Optional<JwtTokenEntity> getTokenByRefreshToken(String refreshToken) {
        return jwtTokenRepository.findByRefreshToken(refreshToken);
    }

    // 로그아웃 시 토큰 삭제
    @Transactional
    public void deleteTokensByUserId(String userId) {
        jwtTokenRepository.deleteByUserId(userId);
    }

    public String createAccessToken(String loginId) {
        return jwtTokenProvider.createAccessToken(loginId);
    }

    public String createRefreshToken(String loginId) {
        return jwtTokenProvider.createRefreshToken(loginId);
    }

    public boolean validateRefreshToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
