package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.ProfileDto.*;
import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.Profile;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.domain.user.repository.ProfileRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public ProfileResponseDto createProfile(ProfileCreateDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        if (dto.getPhoneNumber() != null && profileRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        Profile profile = modelMapper.map(dto, Profile.class);
        profile.setMember(member);
        Profile saved = profileRepository.save(profile);
        return modelMapper.map(saved, ProfileResponseDto.class);
    }

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
        return modelMapper.map(profile, ProfileResponseDto.class);
    }

    @Transactional(readOnly = true)
    public List<ProfileResponseDto> getAllProfiles() {
        return profileRepository.findAll().stream()
                .map(p -> modelMapper.map(p, ProfileResponseDto.class))
                .collect(Collectors.toList());
    }

    public ProfileResponseDto updateProfile(Long id, ProfileUpdateDto dto) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        // 전화번호 변경 시 중복 검사
        if (dto.getPhoneNumber() != null
                && !dto.getPhoneNumber().equals(profile.getPhoneNumber())
                && profileRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setBirthDate(dto.getBirthDate());
        profile.setProfilePicUrl(dto.getProfilePicUrl());

        return modelMapper.map(profile, ProfileResponseDto.class);
    }

    public void deleteProfile(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
        profileRepository.delete(profile);
    }
}
