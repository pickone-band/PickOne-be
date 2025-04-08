package com.PickOne.domain.user.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class MemberAgreement {

    @Column(name = "policy_id", nullable = false)
    private Long policyId;

    @Column(name = "agreed_at", nullable = false)
    private LocalDateTime agreedAt;

    public MemberAgreement(Long policyId, LocalDateTime agreedAt) {
        log.debug("[MemberAgreement 생성자] policyId={}, agreedAt={}", policyId, agreedAt);
        this.policyId = policyId;
        this.agreedAt = agreedAt != null ? agreedAt : LocalDateTime.now();
    }
}
