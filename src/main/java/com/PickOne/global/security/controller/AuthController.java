package com.PickOne.global.security.controller;

import com.PickOne.global.dto.LoginRequest;
import com.PickOne.global.dto.TokenRefreshRequest;
import com.PickOne.global.dto.TokenResponse;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
import com.PickOne.global.security.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<TokenResponse>> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest.getLoginId(), loginRequest.getPassword());
        return BaseResponse.success(SuccessCode.OK, tokenResponse);
    }

    /**
     * 로그아웃 API
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            authService.logout(token);
            return ResponseEntity.ok("로그아웃 성공!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<TokenResponse>> refreshAccessToken(@RequestBody TokenRefreshRequest request) {
        TokenResponse newToken = authService.refreshAccessToken(request.getRefreshToken());
        return BaseResponse.success(SuccessCode.OK, newToken);
    }

}
