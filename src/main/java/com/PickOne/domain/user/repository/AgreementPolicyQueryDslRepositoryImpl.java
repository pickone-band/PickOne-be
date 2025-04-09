package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.AgreementPolicy;
import com.PickOne.domain.user.model.QAgreementPolicy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.PickOne.domain.user.model.QAgreementPolicy.agreementPolicy;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AgreementPolicyQueryDslRepositoryImpl implements AgreementPolicyQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AgreementPolicy> searchPolicies(String title, Boolean isActive, Boolean isRequired) {
        log.info("[AgreementPolicyQueryDslRepositoryImpl.searchPolicies] 약관 검색 시작. title={}, isActive={}, isRequired={}",
                title, isActive, isRequired);

        List<AgreementPolicy> result = queryFactory
                .selectFrom(agreementPolicy)
                .where(
                        title != null ? agreementPolicy.title.containsIgnoreCase(title) : null,
                        isActive != null ? agreementPolicy.isActive.eq(isActive) : null,
                        isRequired != null ? agreementPolicy.isRequired.eq(isRequired) : null
                )
                .fetch();

        log.info("[AgreementPolicyQueryDslRepositoryImpl.searchPolicies] 검색 완료. 조회된 약관 수: {}", result.size());
        return result;
    }

    @Override
    public AgreementPolicy findLatestActivePolicy(String title) {
        log.info("[AgreementPolicyQueryDslRepositoryImpl.findLatestActivePolicy] 최신 활성화 약관 조회 시작. title={}", title);

        AgreementPolicy policy = queryFactory
                .selectFrom(agreementPolicy)
                .where(
                        agreementPolicy.title.eq(title),
                        agreementPolicy.isActive.isTrue()
                )
                .orderBy(agreementPolicy.version.desc())
                .fetchFirst();

        if (policy == null) {
            log.warn("[AgreementPolicyQueryDslRepositoryImpl.findLatestActivePolicy] 활성화된 약관 없음. title={}", title);
        } else {
            log.info("[AgreementPolicyQueryDslRepositoryImpl.findLatestActivePolicy] 조회 성공. ID={}, version={}",
                    policy.getId(), policy.getVersion());
        }
        return policy;
    }
}
