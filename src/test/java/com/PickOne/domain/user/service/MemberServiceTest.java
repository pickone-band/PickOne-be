package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.member.*;
import com.PickOne.domain.user.model.*;
import com.PickOne.domain.user.repository.AgreementPolicyRepository;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AgreementPolicyRepository agreementPolicyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /** 회원가입 성공 - 약관 동의 포함 */
    @Test
    @DisplayName("회원가입 성공 - 약관 동의 포함")
    void createMember_WithAgreements_Success() {
        // Given
        MemberCreateDto createDto = new MemberCreateDto(
                "testuser",
                "password123",
                "testuser@example.com",
                "nickname",
                Arrays.asList(1L, 2L)
        );

        AgreementPolicy policy1 = AgreementPolicy.create("Policy 1", "Content 1", 1L, true, true, LocalDate.now(), null);
        ReflectionTestUtils.setField(policy1, "id", 1L);

        AgreementPolicy policy2 = AgreementPolicy.create("Policy 2", "Content 2", 2L, false, true, LocalDate.now(), null);
        ReflectionTestUtils.setField(policy2, "id", 2L);

        when(agreementPolicyRepository.findAllById(anyList()))
                .thenReturn(Arrays.asList(policy1, policy2));

        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(createDto.getPassword())).thenReturn(encodedPassword);
        when(memberRepository.existsByUsername(createDto.getUsername())).thenReturn(false);
        when(memberRepository.existsByEmail(createDto.getEmail())).thenReturn(false);
        when(memberRepository.existsByNickname(createDto.getNickname())).thenReturn(false);

        Member mockMember = Member.create(
                createDto.getUsername(),
                createDto.getEmail(),
                encodedPassword,
                createDto.getNickname(),
                Arrays.asList(policy1, policy2)
        );
        ReflectionTestUtils.setField(mockMember, "id", 1L);

        when(memberMapper.toEntity(eq(createDto), eq(encodedPassword), anyList())).thenReturn(mockMember);
        when(memberRepository.save(any(Member.class))).thenReturn(mockMember);

        MemberResponseDto responseDto = MemberResponseDto.builder()
                .id(1L)
                .username(createDto.getUsername())
                .email(createDto.getEmail())
                .nickname(createDto.getNickname())
                .role(Role.USER)
                .status(MemberStatus.ACTIVE.name())
                .build();

        when(memberMapper.toDto(any(Member.class))).thenReturn(responseDto);

        // When
        MemberResponseDto result = memberService.createMember(createDto);

        // Then
        assertThat(result.getUsername()).isEqualTo(createDto.getUsername());
        assertThat(result.getEmail()).isEqualTo(createDto.getEmail());
        assertThat(result.getNickname()).isEqualTo(createDto.getNickname());

        verify(memberRepository, times(1)).save(any(Member.class));
        verify(agreementPolicyRepository, times(1)).findAllById(anyList());
    }

    /** 회원가입 실패 - 중복된 사용자 이름 */
    @Test
    @DisplayName("회원가입 실패 - 중복된 사용자 이름")
    void createMember_Fail_DuplicateUsername() {
        // Given
        MemberCreateDto createDto = new MemberCreateDto(
                "testuser",
                "password123",
                "testuser@example.com",
                "nickname",
                Arrays.asList(1L)
        );
        when(memberRepository.existsByUsername(createDto.getUsername())).thenReturn(true);

        // When
        BusinessException exception = assertThrows(BusinessException.class, () ->
                memberService.createMember(createDto)
        );

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_USERNAME);
    }

    /** 회원가입 실패 - 약관 누락 */
    @Test
    @DisplayName("회원가입 실패 - 존재하지 않는 약관")
    void createMember_Fail_TermNotFound() {
        MemberCreateDto createDto = new MemberCreateDto(
                "testuser",
                "password123",
                "testuser@example.com",
                "nickname",
                Arrays.asList(1L, 2L)
        );
        // only 1 policy found, but expected 2
        when(agreementPolicyRepository.findAllById(anyList()))
                .thenReturn(List.of(new AgreementPolicy("Policy 1", "Content 1", 1L)));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                memberService.createMember(createDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TERM_NOT_FOUND);
    }

    /** 단일 회원 조회 성공 */
    @Test
    @DisplayName("단일 회원 조회 성공")
    void getMember_Success() {
        // Given
        Member member = new Member("testuser", "testuser@example.com", "encodedPassword",
                new Profile("nickname", null), Role.USER, new MemberStatusDetail(MemberStatus.ACTIVE));

        MemberResponseDto responseDto = MemberResponseDto.builder()
                .username(member.getUsername())
                .email(member.getEmail())
                .nickname(member.getProfile().getNickname())
                .role(member.getRole())
                .status(member.getStatusDetail().getStatus().name())
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberMapper.toDto(member)).thenReturn(responseDto);

        // When
        MemberResponseDto result = memberService.getMember(1L);

        // Then
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("testuser@example.com");
    }

    /** 단일 회원 조회 실패 */
    @Test
    @DisplayName("단일 회원 조회 실패 - 존재하지 않는 ID")
    void getMember_Fail_NotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                memberService.getMember(1L)
        );
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_INFO_NOT_FOUND);
    }

    /** 전체 회원 조회 성공 */
    @Test
    @DisplayName("전체 회원 조회 성공")
    void getAllMembers_Success() {
        // Given
        Member member1 = new Member("user1", "user1@example.com", "password",
                new Profile("nick1", null), Role.USER, new MemberStatusDetail(MemberStatus.ACTIVE));
        Member member2 = new Member("user2", "user2@example.com", "password",
                new Profile("nick2", null), Role.USER, new MemberStatusDetail(MemberStatus.ACTIVE));

        when(memberRepository.findAll()).thenReturn(List.of(member1, member2));

        MemberResponseDto responseDto1 = MemberResponseDto.builder()
                .username(member1.getUsername())
                .email(member1.getEmail())
                .nickname(member1.getProfile().getNickname())
                .role(member1.getRole())
                .status(member1.getStatusDetail().getStatus().name())
                .build();

        MemberResponseDto responseDto2 = MemberResponseDto.builder()
                .username(member2.getUsername())
                .email(member2.getEmail())
                .nickname(member2.getProfile().getNickname())
                .role(member2.getRole())
                .status(member2.getStatusDetail().getStatus().name())
                .build();

        when(memberMapper.toDto(member1)).thenReturn(responseDto1);
        when(memberMapper.toDto(member2)).thenReturn(responseDto2);

        // When
        List<MemberResponseDto> results = memberService.getAllMembers();

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getUsername()).isEqualTo("user1");
        assertThat(results.get(1).getUsername()).isEqualTo("user2");
    }

    /** 회원 정보 수정 성공 */
    @Test
    @DisplayName("회원 정보 수정 성공")
    void updateMember_Success() {
        // Given
        Member member = new Member("testuser", "testuser@example.com", "encodedPassword",
                new Profile("nickname", null), Role.USER, new MemberStatusDetail(MemberStatus.ACTIVE));

        MemberUpdateDto updateDto = new MemberUpdateDto("newNickname", "newImageUrl", "newPassword");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // When
        memberService.updateMember(1L, updateDto);

        // Then
        assertThat(member.getProfile().getNickname()).isEqualTo("newNickname");
        assertThat(member.getPassword()).isEqualTo("encodedNewPassword");
        assertThat(member.getProfile().getImageUrl()).isEqualTo("newImageUrl");
    }

    /** 회원 삭제 (Soft Delete) 성공 */
    @Test
    @DisplayName("회원 삭제 (Soft Delete) 성공")
    void deleteMember_Success() {
        // Given
        Member member = new Member("testuser", "testuser@example.com", "encodedPassword",
                new Profile("nickname", null), Role.USER, new MemberStatusDetail(MemberStatus.ACTIVE));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // When
        memberService.deleteMember(1L);

        // Then
        assertThat(member.isDeleted()).isTrue();
        assertThat(member.getDeletedAt()).isNotNull();
    }
}
