package com.PickOne.domain.user.dto.aouthAccount;

import com.PickOne.domain.user.model.OAuthAccount;
import com.PickOne.global.util.mapper.BaseMapper;

public class OAuthAccountMapper implements BaseMapper<OAuthAccount, OAuthAccountResponseDto> {

    @Override
    public OAuthAccountResponseDto toDto(OAuthAccount entity) {
        return new OAuthAccountResponseDto(
                entity.getProvider(),
                entity.getProviderId(),
                entity.getEmail(),
                entity.getProfileImage(),
                entity.getName()
        );
    }

    @Override
    public OAuthAccount toEntity(OAuthAccountResponseDto dto) {
        return new OAuthAccount(
                dto.getProvider(),
                dto.getProviderId(),
                dto.getEmail(),
                dto.getProfileImage(),
                dto.getName()
        );
    }
}