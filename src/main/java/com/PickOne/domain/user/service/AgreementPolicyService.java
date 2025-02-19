package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.agreementPolicy.AgreementPolicyCreateDto;
import com.PickOne.domain.user.dto.agreementPolicy.AgreementPolicyMapper;
import com.PickOne.domain.user.dto.agreementPolicy.AgreementPolicyResponseDto;
import com.PickOne.domain.user.model.AgreementPolicy;
import com.PickOne.domain.user.repository.AgreementPolicyQueryDslRepository;
import com.PickOne.domain.user.repository.AgreementPolicyRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AgreementPolicyService {

    private final AgreementPolicyRepository agreementPolicyRepository;
    private final AgreementPolicyQueryDslRepository agreementPolicyQueryDslRepository;
    private final AgreementPolicyMapper agreementPolicyMapper;

    /**
     * [C] 약관 생성
     */
    public AgreementPolicyResponseDto createPolicy(AgreementPolicyCreateDto dto) {
        log.info("[AgreementPolicyService.createPolicy] 약관 생성 시도: title={}, version={}",
                dto.getTitle(), dto.getVersion());

        if (agreementPolicyRepository.existsByTitleAndVersion(dto.getTitle(), dto.getVersion())) {
            log.warn("[createPolicy] 중복된 약관 - title={}, version={}", dto.getTitle(), dto.getVersion());
            throw new BusinessException(ErrorCode.DUPLICATE_TERM_VERSION);
        }

        AgreementPolicy policy = agreementPolicyMapper.toEntity(dto);
        AgreementPolicy savedPolicy = agreementPolicyRepository.save(policy);

        log.info("[createPolicy] 약관 생성 완료 - ID={}, title={}", savedPolicy.getId(), savedPolicy.getTitle());
        return agreementPolicyMapper.toDto(savedPolicy);
    }

    /**
     * [R] 전체 약관 조회
     */
    public List<AgreementPolicyResponseDto> getAllPolicies() {
        log.info("[AgreementPolicyService.getAllPolicies] 전체 약관 조회 시작");
        List<AgreementPolicy> policies = agreementPolicyRepository.findAll();
        log.info("[getAllPolicies] 조회된 약관 수: {}", policies.size());
        return agreementPolicyMapper.toDtoList(policies);
    }

    /**
     * [R] 단일 약관 조회
     */
    public AgreementPolicyResponseDto getPolicy(Long id) {
        log.info("[AgreementPolicyService.getPolicy] 단일 약관 조회 - ID={}", id);
        AgreementPolicy policy = agreementPolicyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[getPolicy] 약관 조회 실패 - 존재하지 않는 ID={}", id);
                    return new BusinessException(ErrorCode.TERM_NOT_FOUND);
                });
        log.info("[getPolicy] 단일 약관 조회 성공 - ID={}, title={}", policy.getId(), policy.getTitle());
        return agreementPolicyMapper.toDto(policy);
    }

    /**
     * [R] 활성화된 약관 조회
     */
    public List<AgreementPolicyResponseDto> getActivePolicies() {
        log.info("[AgreementPolicyService.getActivePolicies] 활성화된 약관 조회 시작");
        List<AgreementPolicy> activePolicies = agreementPolicyRepository.findByIsActiveTrue();
        log.info("[getActivePolicies] 활성화된 약관 수: {}", activePolicies.size());
        return agreementPolicyMapper.toDtoList(activePolicies);
    }

    /**
     * [R] 필수 약관 조회
     */
    public List<AgreementPolicyResponseDto> getRequiredPolicies() {
        log.info("[AgreementPolicyService.getRequiredPolicies] 필수 약관 조회 시작");
        List<AgreementPolicy> requiredPolicies = agreementPolicyRepository.findByIsRequiredTrue();
        log.info("[getRequiredPolicies] 필수 약관 수: {}", requiredPolicies.size());
        return agreementPolicyMapper.toDtoList(requiredPolicies);
    }

    /**
     * [R] 특정 기간 내 약관 조회
     */
    public List<AgreementPolicyResponseDto> getPoliciesByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("[AgreementPolicyService.getPoliciesByDateRange] 기간 내 약관 조회 - startDate={}, endDate={}", startDate, endDate);
        List<AgreementPolicy> policies = agreementPolicyRepository.findByStartDateBeforeAndEndDateAfter(startDate, endDate);
        log.info("[getPoliciesByDateRange] 조회된 약관 수: {}", policies.size());
        return agreementPolicyMapper.toDtoList(policies);
    }

    /**
     * [R] QueryDSL - 동적 검색
     */
    public List<AgreementPolicyResponseDto> searchPolicies(String title, Boolean isActive, Boolean isRequired) {
        log.info("[AgreementPolicyService.searchPolicies] 동적 검색 시작 - title={}, isActive={}, isRequired={}",
                title, isActive, isRequired);
        List<AgreementPolicy> results = agreementPolicyQueryDslRepository.searchPolicies(title, isActive, isRequired);
        log.info("[searchPolicies] 동적 검색 결과 수: {}", results.size());
        return agreementPolicyMapper.toDtoList(results);
    }

    /**
     * [R] QueryDSL - 최신 활성화된 약관
     */
    public AgreementPolicyResponseDto getLatestActivePolicy(String title) {
        log.info("[AgreementPolicyService.getLatestActivePolicy] 최신 활성화 약관 조회 - title={}", title);
        AgreementPolicy latest = agreementPolicyQueryDslRepository.findLatestActivePolicy(title);
        if (latest == null) {
            log.warn("[getLatestActivePolicy] 활성화된 최신 약관 없음 - title={}", title);
            throw new BusinessException(ErrorCode.TERM_NOT_FOUND);
        }
        log.info("[getLatestActivePolicy] 조회 성공 - ID={}, version={}", latest.getId(), latest.getVersion());
        return agreementPolicyMapper.toDto(latest);
    }
}
