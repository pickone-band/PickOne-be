package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.AgreementPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgreementPolicyQueryDslRepository {

    // 동적 조건을 통한 약관 검색
    List<AgreementPolicy> searchPolicies(String title, Boolean isActive, Boolean isRequired);

    // 활성화된 최신 버전 약관 검색
    AgreementPolicy findLatestActivePolicy(String title);
}