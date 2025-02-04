package com.PickOne.domain.user.controller;

import com.PickOne.domain.user.dto.ProfileDto;
import com.PickOne.domain.user.service.ProfileService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    // Profile 조회 API
    @GetMapping("/profiles/{memberId}")
    public ResponseEntity<BaseResponse<ProfileDto.ProfileResponseDto>> getProfile(@PathVariable Long memberId) {
        return BaseResponse.success(SuccessCode.OK, profileService.getProfile(memberId));
    }

    // Profile 수정 API
    @PutMapping("/profiles/{memberId}")
    public ResponseEntity<BaseResponse<ProfileDto.ProfileResponseDto>> updateProfile(
            @PathVariable Long memberId,
            @RequestBody ProfileDto.ProfileUpdateDto updateDto) {
        return BaseResponse.success(SuccessCode.UPDATED, profileService.updateProfile(memberId, updateDto));
    }
}