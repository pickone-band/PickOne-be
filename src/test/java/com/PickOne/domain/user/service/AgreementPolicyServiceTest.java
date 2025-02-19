package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.agreementPolicy.AgreementPolicyCreateDto;
import com.PickOne.domain.user.dto.agreementPolicy.AgreementPolicyMapper;
import com.PickOne.domain.user.dto.agreementPolicy.AgreementPolicyResponseDto;
import com.PickOne.domain.user.model.AgreementPolicy;
import com.PickOne.domain.user.repository.AgreementPolicyQueryDslRepository;
import com.PickOne.domain.user.repository.AgreementPolicyRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // ✅ MockitoExtension 사용
class AgreementPolicyServiceTest {

    @Mock
    private AgreementPolicyRepository agreementPolicyRepository;

    @Mock
    private AgreementPolicyQueryDslRepository agreementPolicyQueryDslRepository;

    @Mock
    private AgreementPolicyMapper agreementPolicyMapper;

    @InjectMocks
    private AgreementPolicyService agreementPolicyService;

    // ✅ @BeforeEach와 openMocks(this) 제거

    /** [C] 약관 생성 테스트 - 성공 */
    @Test
    void createPolicy_Success() {
        // Given
        AgreementPolicyCreateDto createDto = new AgreementPolicyCreateDto(
                "이용약관",
                "이용약관 내용",
                1L,
                true,
                true,
                LocalDate.now(),
                null
        );

        AgreementPolicy policyEntity = AgreementPolicy.create(
                "Privacy Policy",
                "This is the privacy policy content.",
                1L,
                true,
                true,
                LocalDate.now(),
                null
        );
        // ID 직접 세팅 (테스트상 필요)
        ReflectionTestUtils.setField(policyEntity, "id", 1L);

        AgreementPolicyResponseDto responseDto = new AgreementPolicyResponseDto(
                1L,
                createDto.getTitle(),
                createDto.getContent(),
                createDto.getVersion(),
                createDto.isRequired(),
                createDto.isActive(),
                createDto.getStartDate(),
                createDto.getEndDate()
        );

        // Mock 설정
        when(agreementPolicyRepository.existsByTitleAndVersion(createDto.getTitle(), createDto.getVersion()))
                .thenReturn(false);
        when(agreementPolicyMapper.toEntity(createDto)).thenReturn(policyEntity);
        when(agreementPolicyRepository.save(policyEntity)).thenReturn(policyEntity);
        when(agreementPolicyMapper.toDto(policyEntity)).thenReturn(responseDto);

        // When
        AgreementPolicyResponseDto result = agreementPolicyService.createPolicy(createDto);

        // Then
        assertThat(result.getTitle()).isEqualTo(createDto.getTitle());
        verify(agreementPolicyRepository, times(1)).save(policyEntity);
    }

    /** [C] 약관 생성 테스트 - 실패 (중복) */
    @Test
    void createPolicy_Fail_Duplicate() {
        // Given
        AgreementPolicyCreateDto createDto = new AgreementPolicyCreateDto(
                "이용약관",
                "이용약관 내용",
                1L,
                true,
                true,
                LocalDate.now(),
                null
        );
        when(agreementPolicyRepository.existsByTitleAndVersion(createDto.getTitle(), createDto.getVersion()))
                .thenReturn(true);

        // When
        BusinessException exception = assertThrows(BusinessException.class, () ->
                agreementPolicyService.createPolicy(createDto)
        );

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_TERM_VERSION);
        verify(agreementPolicyRepository, never()).save(any());
    }

    /** [R] 전체 약관 조회 */
    @Test
    void getAllPolicies_Success() {
        // Given
        AgreementPolicy policy1 = mock(AgreementPolicy.class);
        AgreementPolicy policy2 = mock(AgreementPolicy.class);
        List<AgreementPolicy> policies = Arrays.asList(policy1, policy2);

        when(agreementPolicyRepository.findAll()).thenReturn(policies);
        when(agreementPolicyMapper.toDtoList(policies)).thenReturn(Arrays.asList(
                new AgreementPolicyResponseDto(1L, "이용약관1", "내용1", 1L, true, true, LocalDate.now(), null),
                new AgreementPolicyResponseDto(2L, "이용약관2", "내용2", 1L, true, true, LocalDate.now(), null)
        ));

        // When
        List<AgreementPolicyResponseDto> result = agreementPolicyService.getAllPolicies();

        // Then
        assertThat(result).hasSize(2);
        verify(agreementPolicyRepository, times(1)).findAll();
    }

    /** [R] 단일 약관 조회 - 성공 */
    @Test
    void getPolicy_Success() {
        // Given
        AgreementPolicy policy = mock(AgreementPolicy.class);
        when(agreementPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(agreementPolicyMapper.toDto(policy)).thenReturn(
                new AgreementPolicyResponseDto(1L, "이용약관", "내용", 1L, true, true, LocalDate.now(), null)
        );

        // When
        AgreementPolicyResponseDto result = agreementPolicyService.getPolicy(1L);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        verify(agreementPolicyRepository, times(1)).findById(1L);
    }

    /** [R] 단일 약관 조회 - 실패 (존재하지 않음) */
    @Test
    void getPolicy_Fail_NotFound() {
        when(agreementPolicyRepository.findById(1L)).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class, () ->
                agreementPolicyService.getPolicy(1L)
        );
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.TERM_NOT_FOUND);
    }

    /** [R] 활성화된 약관 조회 */
    @Test
    void getActivePolicies_Success() {
        List<AgreementPolicy> activePolicies = Arrays.asList(mock(AgreementPolicy.class), mock(AgreementPolicy.class));
        when(agreementPolicyRepository.findByIsActiveTrue()).thenReturn(activePolicies);
        when(agreementPolicyMapper.toDtoList(activePolicies)).thenReturn(Arrays.asList(
                new AgreementPolicyResponseDto(1L, "약관1", "내용1", 1L, true, true, LocalDate.now(), null),
                new AgreementPolicyResponseDto(2L, "약관2", "내용2", 1L, false, true, LocalDate.now(), null)
        ));

        List<AgreementPolicyResponseDto> result = agreementPolicyService.getActivePolicies();

        assertThat(result).hasSize(2);
        verify(agreementPolicyRepository, times(1)).findByIsActiveTrue();
    }

    /** [R] 필수 약관 조회 */
    @Test
    void getRequiredPolicies_Success() {
        List<AgreementPolicy> requiredPolicies = List.of(mock(AgreementPolicy.class));
        when(agreementPolicyRepository.findByIsRequiredTrue()).thenReturn(requiredPolicies);
        when(agreementPolicyMapper.toDtoList(requiredPolicies)).thenReturn(List.of(
                new AgreementPolicyResponseDto(1L, "필수약관", "내용", 1L, true, true, LocalDate.now(), null)
        ));

        List<AgreementPolicyResponseDto> result = agreementPolicyService.getRequiredPolicies();

        assertThat(result).hasSize(1);
        verify(agreementPolicyRepository, times(1)).findByIsRequiredTrue();
    }

    /** [R] 특정 기간 내 약관 조회 */
    @Test
    void getPoliciesByDateRange_Success() {
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(10);

        List<AgreementPolicy> policies = List.of(mock(AgreementPolicy.class));
        when(agreementPolicyRepository.findByStartDateBeforeAndEndDateAfter(startDate, endDate))
                .thenReturn(policies);

        when(agreementPolicyMapper.toDtoList(policies)).thenReturn(List.of(
                new AgreementPolicyResponseDto(1L, "기간약관", "내용", 1L, true, true, startDate, endDate)
        ));

        List<AgreementPolicyResponseDto> result = agreementPolicyService.getPoliciesByDateRange(startDate, endDate);

        assertThat(result).hasSize(1);
        verify(agreementPolicyRepository, times(1)).findByStartDateBeforeAndEndDateAfter(startDate, endDate);
    }

    /** [R] QueryDSL - 동적 검색 */
    @Test
    void searchPolicies_Success() {
        // Given
        List<AgreementPolicy> policies = Arrays.asList(mock(AgreementPolicy.class), mock(AgreementPolicy.class));
        when(agreementPolicyQueryDslRepository.searchPolicies("이용", true, true))
                .thenReturn(policies);
        when(agreementPolicyMapper.toDtoList(policies)).thenReturn(Arrays.asList(
                new AgreementPolicyResponseDto(1L, "이용약관1", "내용1", 1L, true, true, LocalDate.now(), null),
                new AgreementPolicyResponseDto(2L, "이용약관2", "내용2", 1L, true, true, LocalDate.now(), null)
        ));

        // When
        List<AgreementPolicyResponseDto> result = agreementPolicyService.searchPolicies("이용", true, true);

        // Then
        assertThat(result).hasSize(2);
        verify(agreementPolicyQueryDslRepository, times(1))
                .searchPolicies("이용", true, true);
    }

    /** [R] QueryDSL - 최신 활성화된 약관 */
    @Test
    void getLatestActivePolicy_Success() {
        AgreementPolicy policy = mock(AgreementPolicy.class);
        when(policy.getId()).thenReturn(1L);

        when(agreementPolicyQueryDslRepository.findLatestActivePolicy("이용약관"))
                .thenReturn(policy);

        when(agreementPolicyMapper.toDto(policy)).thenReturn(
                new AgreementPolicyResponseDto(1L, "이용약관", "최신 내용", 2L, true, true, LocalDate.now(), null)
        );

        AgreementPolicyResponseDto result = agreementPolicyService.getLatestActivePolicy("이용약관");
        assertThat(result.getTitle()).isEqualTo("이용약관");
        verify(agreementPolicyQueryDslRepository, times(1))
                .findLatestActivePolicy("이용약관");
    }

    @Test
    void getLatestActivePolicy_Fail_NotFound() {
        when(agreementPolicyQueryDslRepository.findLatestActivePolicy("이용약관"))
                .thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            agreementPolicyService.getLatestActivePolicy("이용약관");
        });
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TERM_NOT_FOUND);
    }
}
