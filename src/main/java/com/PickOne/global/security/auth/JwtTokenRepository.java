package com.PickOne.global.security.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JwtTokenEntity, Long> {
    Optional<JwtTokenEntity> findByAccessToken(String accessToken);
    Optional<JwtTokenEntity> findByRefreshToken(String refreshToken);
    void deleteByUserId(String userId);
}
