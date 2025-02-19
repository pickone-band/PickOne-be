package com.PickOne.global.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
    private String loginId;
    private String password;
}
