package com.PickOne.domain.user.dto.memberAgreement;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberAgreementResponseDto {
    private final Long agreementPolicyId;
    private final Long version;
    private final LocalDateTime agreedAt;
}