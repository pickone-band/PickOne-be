package com.PickOne.domain.user.dto.agreementPolicy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class AgreementPolicyCreateDto {
    private final String title;
    private final String content;
    private final Long policyVersion;
    private final boolean isRequired;
    private final boolean isActive;
    private final LocalDate startDate;
    private final LocalDate endDate;
}