package com.PickOne.global.security.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistTokenRepository extends   JpaRepository<BlacklistToken, Long> {
    boolean existsByToken(String token);
}
