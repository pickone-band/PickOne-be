package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.MemberTermDto.MemberTermResponseDto;
import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.MemberTerm;
import com.PickOne.domain.user.model.Term;
import com.PickOne.domain.user.repository.MemberTermRepository;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.domain.user.repository.TermRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito를 활용한 단위 테스트 설정
class MemberTermServiceTest {

    @Mock
    private MemberTermRepository memberTermRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TermRepository termRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MemberTermService memberTermService;

    /**
     * ✅ 회원이 특정 약관에 동의하면 정상적으로 저장되는지 테스트
     */
    @Test
    void 회원이_약관에_동의하면_정상저장된다() {
        // given
        Long memberId = 1L;
        Long termId = 100L;
        Boolean isAgreed = true;

        Member mockMember = Member.builder().id(memberId).build();
        Term mockTerm = Term.builder().id(termId).build();

        MemberTerm mockMemberTerm = MemberTerm.builder()
                .member(mockMember)
                .term(mockTerm)
                .isAgreed(isAgreed)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(termRepository.findById(termId)).thenReturn(Optional.of(mockTerm));
        when(memberTermRepository.save(any(MemberTerm.class))).thenReturn(mockMemberTerm);
        when(modelMapper.map(any(MemberTerm.class), eq(MemberTermResponseDto.class)))
                .thenReturn(new MemberTermResponseDto());

        // when
        MemberTermResponseDto result = memberTermService.createMemberTerm(memberId, termId, isAgreed);

        // then
        assertNotNull(result);
        verify(memberRepository, times(1)).findById(memberId);
        verify(termRepository, times(1)).findById(termId);
        verify(memberTermRepository, times(1)).save(any(MemberTerm.class));
    }

    /**
     * ✅ 존재하지 않는 회원이 약관에 동의하면 예외가 발생해야 한다.
     */
    @Test
    void 존재하지_않는_회원이_약관에_동의하면_예외발생() {
        // given
        Long memberId = 1L;
        Long termId = 100L;
        Boolean isAgreed = true;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> memberTermService.createMemberTerm(memberId, termId, isAgreed)
        );

        assertEquals(ErrorCode.USER_INFO_NOT_FOUND, exception.getErrorCode());
        verify(memberRepository, times(1)).findById(memberId);
        verify(termRepository, never()).findById(termId); // 회원이 없으면 약관 조회도 수행하지 않음
    }

    /**
     * ✅ 특정 회원이 동의한 약관 목록을 정상 조회하는지 테스트
     */
    @Test
    void 특정_회원의_약관_동의내역을_정상조회한다() {
        // given
        Long memberId = 1L;

        Member mockMember = Member.builder().id(memberId).build();
        Term mockTerm1 = Term.builder().id(100L).build();
        Term mockTerm2 = Term.builder().id(101L).build();

        MemberTerm mockMemberTerm1 = MemberTerm.builder().member(mockMember).term(mockTerm1).isAgreed(true).build();
        MemberTerm mockMemberTerm2 = MemberTerm.builder().member(mockMember).term(mockTerm2).isAgreed(true).build();

        when(memberTermRepository.findByMemberId(memberId)).thenReturn(Arrays.asList(mockMemberTerm1, mockMemberTerm2));
        when(modelMapper.map(any(MemberTerm.class), eq(MemberTermResponseDto.class)))
                .thenReturn(new MemberTermResponseDto());

        // when
        List<MemberTermResponseDto> result = memberTermService.getMemberTermsByMember(memberId);

        // then
        assertEquals(2, result.size());
        verify(memberTermRepository, times(1)).findByMemberId(memberId);
    }

    /**
     * ✅ 특정 회원이 동의한 약관이 없을 때 빈 리스트를 반환하는지 테스트
     */
    @Test
    void 특정_회원이_동의한_약관이_없으면_빈리스트반환() {
        // given
        Long memberId = 1L;
        when(memberTermRepository.findByMemberId(memberId)).thenReturn(List.of());

        // when
        List<MemberTermResponseDto> result = memberTermService.getMemberTermsByMember(memberId);

        // then
        assertTrue(result.isEmpty());
        verify(memberTermRepository, times(1)).findByMemberId(memberId);
    }

    /**
     * ✅ 특정 약관에 동의한 회원 목록을 정상 조회하는지 테스트
     */
    @Test
    void 특정_약관에_동의한_회원목록을_정상조회한다() {
        // given
        Long termId = 100L;

        Term mockTerm = Term.builder().id(termId).build();
        Member mockMember1 = Member.builder().id(1L).build();
        Member mockMember2 = Member.builder().id(2L).build();

        MemberTerm mockMemberTerm1 = MemberTerm.builder().member(mockMember1).term(mockTerm).isAgreed(true).build();
        MemberTerm mockMemberTerm2 = MemberTerm.builder().member(mockMember2).term(mockTerm).isAgreed(true).build();

        when(memberTermRepository.findByTermId(termId)).thenReturn(List.of(mockMemberTerm1, mockMemberTerm2));
        when(modelMapper.map(any(MemberTerm.class), eq(MemberTermResponseDto.class)))
                .thenReturn(new MemberTermResponseDto());

        // when
        List<MemberTermResponseDto> result = memberTermService.getMemberTermsByTerm(termId);

        // then
        assertEquals(2, result.size());
        verify(memberTermRepository, times(1)).findByTermId(termId);
    }
}
