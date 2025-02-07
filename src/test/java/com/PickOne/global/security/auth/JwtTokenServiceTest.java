package com.PickOne.global.security.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenServiceTest {

    private JwtTokenRepository jwtTokenRepository;
    private JwtTokenProvider jwtTokenProvider;
    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenRepository = mock(JwtTokenRepository.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        jwtTokenService = new JwtTokenService(jwtTokenRepository, jwtTokenProvider);
    }

    @Test
    void saveToken_StoresTokenInRepository() {
        // Given
        String userId = "testUser";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        // When
        jwtTokenService.saveToken(userId, accessToken, refreshToken, expiresAt);

        // Then
        verify(jwtTokenRepository, times(1)).deleteByUserId(userId);
        verify(jwtTokenRepository, times(1)).save(Mockito.any());
    }

    @Test
    void validateRefreshToken_ValidToken_ReturnsTrue() {
        // Given
        String refreshToken = "validToken";
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);

        // When
        boolean isValid = jwtTokenService.validateRefreshToken(refreshToken);

        // Then
        assertTrue(isValid);
    }

    @Test
    void deleteTokensByUserId_DeletesTokensFromRepository() {
        // Given
        String userId = "testUser";

        // When
        jwtTokenService.deleteTokensByUserId(userId);

        // Then
        verify(jwtTokenRepository, times(1)).deleteByUserId(userId);
    }
}
