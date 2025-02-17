package com.PickOne.global.security.auth;

import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.global.dto.TokenResponse;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenService jwtTokenService;
    private BlacklistTokenRepository blacklistTokenRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtTokenService = mock(JwtTokenService.class);
        blacklistTokenRepository = mock(BlacklistTokenRepository.class);
        authService = new AuthService(memberRepository, passwordEncoder, jwtTokenService, blacklistTokenRepository);
    }

    @Test
    void login_ValidCredentials_ReturnsTokenResponse() {
        // Given
        String loginId = "testUser";
        String password = "password123";
        Member member = Member.builder().loginId(loginId).password("encodedPassword").build();

        when(memberRepository.findByLoginId(loginId)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
        when(jwtTokenService.createAccessToken(loginId)).thenReturn("accessToken");
        when(jwtTokenService.createRefreshToken(loginId)).thenReturn("refreshToken");

        // When
        TokenResponse response = authService.login(loginId, password);

        // Then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Given
        String loginId = "testUser";
        String password = "wrongPassword";
        Member member = Member.builder().loginId(loginId).password("encodedPassword").build();

        when(memberRepository.findByLoginId(loginId)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        // When & Then
        assertThrows(BusinessException.class, () -> authService.login(loginId, password));
    }

    @Test
    void logout_ValidToken_SavesToBlacklist() {
        // Given
        String token = "Bearer accessToken";
        String userId = "testUser";

        JwtTokenEntity tokenEntity = JwtTokenEntity.builder().userId(userId).expiresAt(null).build();
        when(jwtTokenService.getTokenByAccessToken("accessToken")).thenReturn(Optional.of(tokenEntity));

        // When
        authService.logout(token);

        // Then
        verify(jwtTokenService, times(1)).deleteTokensByUserId(userId);
        verify(blacklistTokenRepository, times(1)).save(Mockito.any());
    }

    @Test
    void refreshAccessToken_ValidRefreshToken_ReturnsNewAccessToken() {
        // Given
        String refreshToken = "refreshToken";
        String userId = "testUser";

        JwtTokenEntity tokenEntity = JwtTokenEntity.builder().userId(userId).build();
        when(jwtTokenService.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtTokenService.getTokenByRefreshToken(refreshToken)).thenReturn(Optional.of(tokenEntity));
        when(jwtTokenService.createAccessToken(userId)).thenReturn("newAccessToken");

        // When
        TokenResponse response = authService.refreshAccessToken(refreshToken);

        // Then
        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
    }
}
