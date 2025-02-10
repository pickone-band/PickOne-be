package com.PickOne.domain.user.dto.memberAgreement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class MemberAgreementCreateDto {
    private final Long agreementPolicyId;
    private final Long policyVersion;
    private final LocalDateTime agreedAt;
}