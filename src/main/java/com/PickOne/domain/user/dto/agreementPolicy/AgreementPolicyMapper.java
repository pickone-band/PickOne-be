package com.PickOne.domain.user.dto.agreementPolicy;

import com.PickOne.domain.user.model.AgreementPolicy;
import com.PickOne.global.util.mapper.BaseMapper;

public class AgreementPolicyMapper implements BaseMapper<AgreementPolicy, AgreementPolicyResponseDto> {

    @Override
    public AgreementPolicyResponseDto toDto(AgreementPolicy entity) {
        return new AgreementPolicyResponseDto(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getVersion(),
                entity.isRequired(),
                entity.isActive(),
                entity.getStartDate(),
                entity.getEndDate()
        );
    }

    @Override
    public AgreementPolicy toEntity(AgreementPolicyResponseDto dto) {
        return new AgreementPolicy(
                dto.getTitle(),
                dto.getContent(),
                dto.getPolicyVersion()
        );
    }

    public AgreementPolicy toEntity(AgreementPolicyCreateDto dto) {
        return new AgreementPolicy(
                dto.getTitle(),
                dto.getContent(),
                dto.getPolicyVersion()
        );
    }

}