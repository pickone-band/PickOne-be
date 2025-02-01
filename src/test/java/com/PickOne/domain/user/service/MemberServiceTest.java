package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.MemberDto.*;
import com.PickOne.domain.user.dto.MemberTermDto.*;
import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.MemberTerm;
import com.PickOne.domain.user.model.Role;
import com.PickOne.domain.user.model.Term;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.domain.user.repository.MemberTermRepository;
import com.PickOne.domain.user.repository.TermRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * MemberService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberTermRepository memberTermRepository;

    @Mock
    private TermRepository termRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private MemberCreateDto memberCreateDto;
    private Member member;
    private Term requiredTerm;
    private Term optionalTerm;

    @BeforeEach
    void setUp() {
        // 필수 약관
        requiredTerm = Term.builder()
                .id(1L)
                .isRequired(true)
                .build();

        // 선택 약관
        optionalTerm = Term.builder()
                .id(2L)
                .isRequired(false)
                .build();

        // 회원가입 DTO (Builder 사용)
        memberCreateDto = MemberCreateDto.builder()
                .loginId("testLogin")
                .password("rawPassword")
                .username("Test User")
                .email("test@example.com")
                .nickname("testNickname")
                .termAgreements(asList(
                        MemberTermCreateDto.builder().termId(1L).isAgreed(true).build(),
                        MemberTermCreateDto.builder().termId(2L).isAgreed(false).build()
                ))
                .build();

        // 엔티티 (Builder 사용)
        member = Member.builder()
                .id(100L)
                .loginId("testLogin")
                .password("encodedPassword")
                .username("Test User")
                .email("test@example.com")
                .nickname("testNickname")
                .role(Role.USER)
                .build();
    }

    /* ===================================================================
     * 1) createMember
     * =================================================================== */
    @Test
    void createMember_Success() {
        // given
        when(memberRepository.existsByLoginId("testLogin")).thenReturn(false);
        when(memberRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(memberRepository.existsByNickname("testNickname")).thenReturn(false);
        when(termRepository.findAll()).thenReturn(asList(requiredTerm, optionalTerm));

        // modelMapper
        when(modelMapper.map(memberCreateDto, Member.class)).thenReturn(member);
        // 패스워드 암호화
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        // 회원 저장
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // 약관 저장 시, findById 동작
        when(termRepository.findById(1L)).thenReturn(Optional.of(requiredTerm));
        when(termRepository.findById(2L)).thenReturn(Optional.of(optionalTerm));

        // when
        memberService.createMember(memberCreateDto);

        // then
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(memberTermRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createMember_Fail_DuplicateLoginId() {
        // given
        when(memberRepository.existsByLoginId("testLogin")).thenReturn(true);

        // when & then
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> memberService.createMember(memberCreateDto)
        );
        assertEquals(ErrorCode.DUPLICATE_LOGIN_ID, ex.getErrorCode());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void createMember_Fail_RequiredTermNotAgreed() {
        // given
        // 필수 약관 동의 해제
        memberCreateDto.getTermAgreements().get(0).setIsAgreed(false);

        when(memberRepository.existsByLoginId("testLogin")).thenReturn(false);
        when(memberRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(memberRepository.existsByNickname("testNickname")).thenReturn(false);
        when(termRepository.findAll()).thenReturn(asList(requiredTerm, optionalTerm));

        // when & then
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> memberService.createMember(memberCreateDto)
        );
        assertEquals(ErrorCode.REQUIRED_TERM_NOT_AGREED, ex.getErrorCode());
        verify(memberRepository, never()).save(any(Member.class));
    }

    /* ===================================================================
     * 2) getAllMembers
     * =================================================================== */
    @Test
    void getAllMembers_Success() {
        // given
        Member member2 = Member.builder()
                .id(200L)
                .loginId("testLogin2")
                .password("pwd2")
                .username("User2")
                .email("test2@example.com")
                .nickname("nick2")
                .role(Role.USER)
                .build();

        when(memberRepository.findAll()).thenReturn(asList(member, member2));

        MemberResponseDto dto1 = MemberResponseDto.builder()
                .id(100L)
                .loginId("testLogin")
                .username("Test User")
                .email("test@example.com")
                .nickname("testNickname")
                .role(Role.USER)
                .build();

        MemberResponseDto dto2 = MemberResponseDto.builder()
                .id(200L)
                .loginId("testLogin2")
                .username("User2")
                .email("test2@example.com")
                .nickname("nick2")
                .role(Role.USER)
                .build();

        when(modelMapper.map(member, MemberResponseDto.class)).thenReturn(dto1);
        when(modelMapper.map(member2, MemberResponseDto.class)).thenReturn(dto2);

        // when
        List<MemberResponseDto> results = memberService.getAllMembers();

        // then
        assertEquals(2, results.size());
        assertEquals(100L, results.get(0).getId());
        assertEquals(200L, results.get(1).getId());
    }

    /* ===================================================================
     * 3) getMember
     * =================================================================== */
    @Test
    void getMember_Success() {
        // given
        when(memberRepository.findById(100L)).thenReturn(Optional.of(member));

        MemberResponseDto dto = MemberResponseDto.builder()
                .id(100L)
                .loginId("testLogin")
                .username("Test User")
                .email("test@example.com")
                .nickname("testNickname")
                .role(Role.USER)
                .build();

        when(modelMapper.map(member, MemberResponseDto.class)).thenReturn(dto);

        // when
        MemberResponseDto response = memberService.getMember(100L);

        // then
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("testLogin", response.getLoginId());
    }

    @Test
    void getMember_Fail_UserNotFound() {
        // given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> memberService.getMember(999L)
        );
        assertEquals(ErrorCode.USER_INFO_NOT_FOUND, ex.getErrorCode());
    }

    /* ===================================================================
     * 4) updateMember
     * =================================================================== */
    @Test
    void updateMember_Success() {
        // given
        MemberUpdateDto updateDto = MemberUpdateDto.builder()
                .nickname("newNickname")
                .username("New Username")
                .build();

        when(memberRepository.findById(100L)).thenReturn(Optional.of(member));
        when(memberRepository.existsByNickname("newNickname")).thenReturn(false);

        Member updatedMember = Member.builder()
                .id(100L)
                .loginId("testLogin")
                .password("encodedPassword")
                .username("New Username")
                .email("test@example.com")
                .nickname("newNickname")
                .role(Role.USER)
                .build();

        when(modelMapper.map(member, MemberResponseDto.class)).thenReturn(
                MemberResponseDto.builder()
                        .id(100L)
                        .loginId("testLogin")
                        .username("New Username")
                        .email("test@example.com")
                        .nickname("newNickname")
                        .role(Role.USER)
                        .build()
        );

        // when
        MemberResponseDto result = memberService.updateMember(100L, updateDto);

        // then
        assertEquals("New Username", result.getUsername());
        assertEquals("newNickname", result.getNickname());
    }

    @Test
    void updateMember_Fail_DuplicateNickname() {
        // given
        MemberUpdateDto updateDto = MemberUpdateDto.builder()
                .nickname("duplicateNick")
                .username("Username")
                .build();

        when(memberRepository.findById(100L)).thenReturn(Optional.of(member));
        when(memberRepository.existsByNickname("duplicateNick")).thenReturn(true);

        // when & then
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> memberService.updateMember(100L, updateDto)
        );
        assertEquals(ErrorCode.DUPLICATE_NICKNAME, ex.getErrorCode());
    }

    /* ===================================================================
     * 5) deleteMember
     * =================================================================== */
    @Test
    void deleteMember_Success() {
        // given
        when(memberRepository.findById(100L)).thenReturn(Optional.of(member));

        // when
        memberService.deleteMember(100L);

        // then
        verify(memberRepository, times(1)).delete(member);
    }

    @Test
    void deleteMember_Fail_UserNotFound() {
        // given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> memberService.deleteMember(999L)
        );
        assertEquals(ErrorCode.USER_INFO_NOT_FOUND, ex.getErrorCode());
        verify(memberRepository, never()).delete(any(Member.class));
    }
}
