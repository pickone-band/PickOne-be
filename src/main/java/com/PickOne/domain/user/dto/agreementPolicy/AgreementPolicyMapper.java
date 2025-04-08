package com.PickOne.domain.user.dto.agreementPolicy;

import com.PickOne.domain.user.model.AgreementPolicy;
import com.PickOne.global.util.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
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
                dto.getVersion()
        );
    }

    public AgreementPolicy toEntity(AgreementPolicyCreateDto dto) {
        return new AgreementPolicy(
                dto.getTitle(),
                dto.getContent(),
                dto.getVersion()
        );
    }

    /**
     * ✅ 리스트 변환 - Entity → DTO
     */
    public List<AgreementPolicyResponseDto> toDtoList(List<AgreementPolicy> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 리스트 변환 - DTO → Entity
     */
    public List<AgreementPolicy> toEntityList(List<AgreementPolicyResponseDto> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
