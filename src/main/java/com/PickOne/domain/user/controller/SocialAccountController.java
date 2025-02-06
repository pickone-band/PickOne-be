package com.PickOne.domain.user.controller;

import com.PickOne.domain.user.dto.SocialAccountDto.*;
import com.PickOne.domain.user.service.SocialAccountService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social-accounts")
@RequiredArgsConstructor
public class SocialAccountController {

    private final SocialAccountService socialAccountService;

    @PostMapping
    public ResponseEntity<BaseResponse<SocialAccountResponseDto>> create(@RequestBody SocialAccountCreateDto dto) {
        SocialAccountResponseDto saved = socialAccountService.createSocialAccount(dto);
        return BaseResponse.success(saved);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<SocialAccountResponseDto>>> getAll() {
        List<SocialAccountResponseDto> list = socialAccountService.getAllSocialAccounts();
        return BaseResponse.success(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<SocialAccountResponseDto>> getOne(@PathVariable Long id) {
        SocialAccountResponseDto dto = socialAccountService.getSocialAccount(id);
        return BaseResponse.success(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<SocialAccountResponseDto>> update(@PathVariable Long id,
                                                                         @RequestBody SocialAccountUpdateDto dto) {
        SocialAccountResponseDto updated = socialAccountService.updateSocialAccount(id, dto);
        return BaseResponse.success(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        socialAccountService.deleteSocialAccount(id);
        return BaseResponse.success(SuccessCode.DELETED);
    }
}
