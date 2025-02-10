package com.PickOne.domain.user.dto.profile;

import com.PickOne.domain.user.model.Profile;
import com.PickOne.global.util.mapper.BaseMapper;

public class ProfileMapper implements BaseMapper<Profile, ProfileResponseDto> {

    @Override
    public ProfileResponseDto toDto(Profile entity) {
        return new ProfileResponseDto(
                entity.getName(),
                entity.getImageUrl()
        );
    }

    @Override
    public Profile toEntity(ProfileResponseDto dto) {
        return dto != null ? new Profile(dto.getName(), dto.getImageUrl()) : new Profile("", null);
    }

    public Profile toEntity(ProfileUpdateDto dto) {
        return dto != null ? new Profile(dto.getName(), dto.getImageUrl()) : new Profile("", null);
    }
}