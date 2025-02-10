package com.PickOne.domain.user.dto.agreementPolicy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AgreementPolicyResponseDto {
    private final Long id;
    private final String title;
    private final String content;
    private final Long policyVersion;
    private final boolean isRequired;
    private final boolean isActive;
    private final LocalDate startDate;
    private final LocalDate endDate;
}