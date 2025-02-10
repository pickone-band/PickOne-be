package com.PickOne.domain.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAgreement {

    @Column(name = "policy_id", nullable = false)
    private Long policyId;

    @Column(name = "policy_version", nullable = false)
    private Long policyVersion;

    @Column(name = "agreed_at", nullable = false)
    private LocalDateTime agreedAt;

    public MemberAgreement(Long policyId, Long policyVersion, LocalDateTime agreedAt) {
        this.policyId = policyId;
        this.policyVersion = policyVersion;
        this.agreedAt = agreedAt;
    }

}
