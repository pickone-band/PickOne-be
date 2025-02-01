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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberTermRepository memberTermRepository;
    private final TermRepository termRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    /**
     * 회원가입
     */
    public void createMember(MemberCreateDto dto) {
        // 1) 중복 체크
        validateDuplicate(dto);

        // 2) 필수 약관 체크
        validateRequiredTerms(dto.getTermAgreements());

        // 3) Member 엔티티 생성
        Member member = modelMapper.map(dto, Member.class);
        member.setRole(Role.USER);
        member.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 4) Member 저장
        Member savedMember = memberRepository.save(member);

        // 5) 약관 동의 내역 저장 (MemberTerm)
        saveMemberTerms(savedMember, dto.getTermAgreements());
    }

    /**
     * 필수 약관에 모두 동의했는지 검증
     */
    private void validateRequiredTerms(List<MemberTermCreateDto> termAgreements) {
        // (1) DB에서 '필수' 약관 목록 조회
        List<Term> requiredTerms = termRepository.findAll().stream()
                .filter(Term::getIsRequired)
                .toList();

        // (2) 요청에서 termId -> isAgreed 매핑
        Map<Long, Boolean> agreementMap = termAgreements.stream()
                .collect(Collectors.toMap(MemberTermCreateDto::getTermId, MemberTermCreateDto::getIsAgreed));

        // (3) 모든 필수 약관에 대해 동의(true)인지 검사
        for (Term requiredTerm : requiredTerms) {
            Boolean agreed = agreementMap.get(requiredTerm.getId());
            if (agreed == null || !agreed) {
                throw new BusinessException(ErrorCode.REQUIRED_TERM_NOT_AGREED);
            }
        }
    }

    /**
     * 회원-약관 동의 내역(MemberTerm) 저장
     */
    private void saveMemberTerms(Member savedMember, List<MemberTermCreateDto> termAgreements) {
        List<MemberTerm> memberTerms = termAgreements.stream()
                .map(agreement -> {
                    Term term = termRepository.findById(agreement.getTermId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.TERM_NOT_FOUND));

                    return MemberTerm.builder()
                            .member(savedMember)
                            .term(term)
                            .isAgreed(agreement.getIsAgreed())
                            .build();
                })
                .collect(Collectors.toList());

        // Bulk insert 처리
        memberTermRepository.saveAll(memberTerms);
    }

    private void validateDuplicate(MemberCreateDto dto) {
        if (memberRepository.existsByLoginId(dto.getLoginId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (memberRepository.existsByNickname(dto.getNickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    @Transactional
    public List<MemberResponseDto> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(m -> modelMapper.map(m, MemberResponseDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public MemberResponseDto getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        return modelMapper.map(member, MemberResponseDto.class);
    }


    @Transactional
    public MemberResponseDto updateMember(Long id, MemberUpdateDto dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        // 닉네임 중복
        if (!member.getNickname().equals(dto.getNickname())
                && memberRepository.existsByNickname(dto.getNickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
        if(member.getUsername() != null ){
            member.setUsername(dto.getUsername());
        }
        if(member.getNickname() != null) {
            member.setNickname(dto.getNickname());
        }
        return modelMapper.map(member, MemberResponseDto.class);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        memberRepository.delete(member);
    }
}


