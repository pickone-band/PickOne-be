package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.ProfileDto.*;
import com.PickOne.domain.user.model.Profile;
import com.PickOne.domain.user.repository.ProfileRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;

    public ProfileResponseDto getProfile(Long memberId) {
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        return modelMapper.map(profile, ProfileResponseDto.class);
    }

    @Transactional
    public ProfileResponseDto updateProfile(Long memberId, ProfileUpdateDto updateDto) {
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        modelMapper.map(updateDto, profile);
        return modelMapper.map(profile, ProfileResponseDto.class);
    }
}