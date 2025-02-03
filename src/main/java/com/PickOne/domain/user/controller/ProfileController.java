package com.PickOne.domain.user.controller;

import com.PickOne.domain.user.dto.ProfileDto;
import com.PickOne.domain.user.dto.ProfileDto.*;
import com.PickOne.domain.user.service.ProfileService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<BaseResponse<ProfileResponseDto>> create(@RequestBody ProfileCreateDto dto) {
        ProfileResponseDto saved = profileService.createProfile(dto);
        return BaseResponse.success(saved);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<ProfileResponseDto>>> getAll() {
        List<ProfileResponseDto> list = profileService.getAllProfiles();
        return BaseResponse.success(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProfileResponseDto>> getOne(@PathVariable Long id) {
        ProfileResponseDto dto = profileService.getProfile(id);
        return BaseResponse.success(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ProfileResponseDto>> update(@PathVariable Long id,
                                                                   @RequestBody ProfileUpdateDto dto) {
        ProfileResponseDto updated = profileService.updateProfile(id, dto);
        return BaseResponse.success(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        profileService.deleteProfile(id);
        return BaseResponse.success(SuccessCode.DELETED);
    }
}
