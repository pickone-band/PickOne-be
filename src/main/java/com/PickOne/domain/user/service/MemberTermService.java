package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.MemberDto;
import com.PickOne.domain.user.dto.MemberTermDto.*;
import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.MemberTerm;
import com.PickOne.domain.user.model.Term;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.domain.user.repository.MemberTermRepository;
import com.PickOne.domain.user.repository.TermRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberTermService {

    private final MemberTermRepository memberTermRepository;
    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final ModelMapper modelMapper;

    /**
     * 약관 동의 정보 생성
     */
    public MemberTermResponseDto createMemberTerm(Long memberId, Long termId, Boolean isAgreed) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TERM_NOT_FOUND));

        // MemberTerm 엔티티 생성
        MemberTerm memberTerm = MemberTerm.builder()
                .member(member)
                .term(term)
                .isAgreed(isAgreed)
                .build();

        MemberTerm saved = memberTermRepository.save(memberTerm);

        return modelMapper.map(saved, MemberTermResponseDto.class);
    }

    /**
     * 특정 MemberTerm 조회
     */
    public MemberTermResponseDto getMemberTerm(Long memberTermId) {
        MemberTerm memberTerm = memberTermRepository.findById(memberTermId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_TERM_NOT_FOUND)); // 필요 시 에러코드 수정

        return modelMapper.map(memberTerm, MemberTermResponseDto.class);
    }

    /**
     * 모든 MemberTerm 조회
     */
    public List<MemberTermResponseDto> getAllMemberTerms() {
        return memberTermRepository.findAll().stream()
                .map(mt -> modelMapper.map(mt, MemberTermResponseDto.class))
                .collect(Collectors.toList());
    }

    /**
     * 특정 회원의 약관 동의 내역 조회
     */
    public List<MemberTermResponseDto> getMemberTermsByMember(Long memberId) {
        List<MemberTerm> memberTerms = memberTermRepository.findByMemberId(memberId);

        return memberTerms.stream()
                .map(mt -> modelMapper.map(mt, MemberTermResponseDto.class))
                .collect(Collectors.toList());
    }

    /**
     * 특정 약관에 동의한 회원 목록 조회
     */
    public List<MemberTermResponseDto> getMemberTermsByTerm(Long termId) {
        List<MemberTerm> memberTerms = memberTermRepository.findByTermId(termId);

        return memberTerms.stream()
                .map(mt -> modelMapper.map(mt, MemberTermResponseDto.class))
                .collect(Collectors.toList());
    }


}
