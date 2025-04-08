package com.PickOne.domain.user.dto.memberAgreement;


import com.PickOne.domain.user.model.MemberAgreement;
import com.PickOne.global.util.mapper.BaseMapper;

public class MemberAgreementMapper implements BaseMapper<MemberAgreement, MemberAgreementResponseDto> {

    @Override
    public MemberAgreementResponseDto toDto(MemberAgreement entity) {
        return new MemberAgreementResponseDto(
                entity.getPolicyId(),
                entity.getAgreedAt()
        );
    }

    @Override
    public MemberAgreement toEntity(MemberAgreementResponseDto dto) {
        return new MemberAgreement(
                dto.getAgreementPolicyId(),
                dto.getAgreedAt()
        );
    }

    public MemberAgreement toEntity(MemberAgreementCreateDto dto) {
        return new MemberAgreement(
                dto.getAgreementPolicyId(),
                dto.getAgreedAt()
        );
    }
}