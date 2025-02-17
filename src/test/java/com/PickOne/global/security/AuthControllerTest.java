package com.PickOne.global.security.controller;

import com.PickOne.global.dto.LoginRequest;
import com.PickOne.global.dto.TokenRefreshRequest;
import com.PickOne.global.dto.TokenResponse;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.security.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;

class AuthControllerTest {

    private AuthService authService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        authController = new AuthController(authService);
    }

    @Test
    void login_ValidRequest_ReturnsTokenResponse() {
        // Given
        LoginRequest loginRequest = LoginRequest.builder().loginId("loginId").password("password123").build();
        TokenResponse tokenResponse = new TokenResponse("accessToken", "refreshToken");

        when(authService.login("testUser", "password123")).thenReturn(tokenResponse);

        // When
        var response = authController.login(loginRequest);

        // Then
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void logout_ValidToken_ReturnsSuccessMessage() {
        // Given
        String token = "Bearer accessToken";
        doNothing().when(authService).logout(token);

        // When
        var response = authController.logout(token);

        // Then
        assertEquals(OK, response.getStatusCode());
    }
}
