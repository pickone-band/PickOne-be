package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.AgreementPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgreementPolicyRepository extends JpaRepository<AgreementPolicy, Long>, AgreementPolicyQueryDslRepository {

    // 제목과 버전으로 약관 조회
    Optional<AgreementPolicy> findByTitleAndVersion(String title, Long version);

    // 활성화된 약관 목록 조회
    List<AgreementPolicy> findByIsActiveTrue();

    // 필수 약관 목록 조회
    List<AgreementPolicy> findByIsRequiredTrue();

    // 특정 기간 내의 약관 조회
    List<AgreementPolicy> findByStartDateBeforeAndEndDateAfter(LocalDate startDate, LocalDate endDate);

    // 약관 제목+버전 중복 확인
    boolean existsByTitleAndVersion(String title, Long version);
}
