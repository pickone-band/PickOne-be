package com.PickOne.global.dto;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}