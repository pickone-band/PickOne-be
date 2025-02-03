package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.ProfileDto;
import com.PickOne.domain.user.model.Profile;
import com.PickOne.domain.user.repository.ProfileRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProfileServiceTest {

    @MockBean
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileService profileService;

    @MockBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("성공적으로 프로필 조회")
    void testGetProfile_Success() {
        Profile profile = Profile.builder().id(1L).build();

        when(profileRepository.findByMemberId(1L)).thenReturn(Optional.of(profile));
        when(modelMapper.map(profile, ProfileDto.ProfileResponseDto.class)).thenReturn(ProfileDto.ProfileResponseDto.builder().id(1L).build());

        ProfileDto.ProfileResponseDto response = profileService.getProfile(1L);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("존재하지 않는 프로필 조회 시 예외 발생")
    void testGetProfile_NotFound() {
        when(profileRepository.findByMemberId(1L)).thenReturn(Optional.empty());

        try {
            profileService.getProfile(1L);
        } catch (BusinessException e) {
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.USER_INFO_NOT_FOUND);
        }
    }

    @Test
    @DisplayName("성공적으로 프로필 수정")
    void testUpdateProfile_Success() {
        Profile profile = Profile.builder().id(1L).build();
        ProfileDto.ProfileUpdateDto updateDto = ProfileDto.ProfileUpdateDto.builder().phoneNumber("010-1234-5678").build();

        when(profileRepository.findByMemberId(1L)).thenReturn(Optional.of(profile));
        when(modelMapper.map(profile, ProfileDto.ProfileResponseDto.class)).thenReturn(ProfileDto.ProfileResponseDto.builder().id(1L).build());

        ProfileDto.ProfileResponseDto response = profileService.updateProfile(1L, updateDto);
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 프로필 수정 시 예외 발생")
    void testUpdateProfile_NotFound() {
        ProfileDto.ProfileUpdateDto updateDto = ProfileDto.ProfileUpdateDto.builder().phoneNumber("010-1234-5678").build();
        when(profileRepository.findByMemberId(1L)).thenReturn(Optional.empty());

        try {
            profileService.updateProfile(1L, updateDto);
        } catch (BusinessException e) {
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.USER_INFO_NOT_FOUND);
        }
    }
}
