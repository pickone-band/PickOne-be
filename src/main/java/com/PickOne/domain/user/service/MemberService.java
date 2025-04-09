package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.member.*;
import com.PickOne.domain.user.model.AgreementPolicy;
import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.repository.AgreementPolicyRepository;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final AgreementPolicyRepository agreementPolicyRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberMapper memberMapper;

    /**
     * ✅ 회원가입
     */
    public MemberResponseDto createMember(MemberCreateDto dto) {
        log.info("[MemberService.createMember] 회원가입 시도: username={}, email={}",
                dto.getUsername(), dto.getEmail());

        // 1) 중복 체크
        validateDuplicate(dto);
        log.debug("[createMember] 중복 체크 통과: username={}", dto.getUsername());

        // 2) 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        log.debug("[createMember] 비밀번호 암호화 완료: encodedPassword={}", encodedPassword);

        // 3) 약관 조회
        List<AgreementPolicy> policies = agreementPolicyRepository.findAllById(dto.getAgreementPolicyIds());
        log.debug("[createMember] 약관 조회 결과: 요청 개수={}, 실제 조회 개수={}",
                dto.getAgreementPolicyIds().size(), policies.size());
        if (policies.size() != dto.getAgreementPolicyIds().size()) {
            log.error("[createMember] 약관 일부 누락됨. TERM_NOT_FOUND 발생");
            throw new BusinessException(ErrorCode.TERM_NOT_FOUND);
        }

        // 4) 회원 생성
        Member member = memberMapper.toEntity(dto, encodedPassword, policies);
        log.debug("[createMember] 매퍼로 엔티티 변환 완료: {}", member);

        // 5) 저장
        Member savedMember = memberRepository.save(member);
        log.info("[createMember] 회원가입 성공 - ID={}, Username={}", savedMember.getId(), savedMember.getUsername());

        // 6) DTO 변환 후 반환
        MemberResponseDto responseDto = memberMapper.toDto(savedMember);
        log.debug("[createMember] 엔티티→DTO 변환 완료: {}", responseDto);
        return responseDto;
    }

    /**
     * 중복 검사
     */
    private void validateDuplicate(MemberCreateDto dto) {
        log.debug("[MemberService.validateDuplicate] 중복 체크: username={}, email={}, nickname={}",
                dto.getUsername(), dto.getEmail(), dto.getNickname());
        if (memberRepository.existsByUsername(dto.getUsername())) {
            log.warn("[validateDuplicate] 중복된 사용자 이름: {}", dto.getUsername());
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (memberRepository.existsByEmail(dto.getEmail())) {
            log.warn("[validateDuplicate] 중복된 이메일: {}", dto.getEmail());
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (memberRepository.existsByNickname(dto.getNickname())) {
            log.warn("[validateDuplicate] 중복된 닉네임: {}", dto.getNickname());
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    /**
     * ✅ 단일 회원 조회
     */
    @Transactional
    public MemberResponseDto getMember(Long id) {
        log.info("[MemberService.getMember] 단일 회원 조회 시도 - ID={}", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[getMember] 회원 조회 실패 - 존재하지 않는 ID={}", id);
                    return new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
                });
        log.info("[getMember] 단일 회원 조회 성공 - ID={}, username={}", member.getId(), member.getUsername());
        return memberMapper.toDto(member);
    }

    /**
     * ✅ 전체 회원 조회
     */
    @Transactional
    public List<MemberResponseDto> getAllMembers() {
        log.info("[MemberService.getAllMembers] 전체 회원 조회 시작");
        List<Member> members = memberRepository.findAll();
        log.info("[getAllMembers] 조회된 회원 수: {}", members.size());
        return members.stream()
                .map(memberMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 회원 정보 수정
     */
    @Transactional
    public MemberResponseDto updateMember(Long id, MemberUpdateDto dto) {
        log.info("[MemberService.updateMember] 회원 정보 수정 시도 - ID={}, dto={}", id, dto);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[updateMember] 회원 조회 실패 - 존재하지 않는 ID={}", id);
                    return new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
                });

        // 닉네임 중복 체크
        if (dto.getNickname() != null
                && !dto.getNickname().equals(member.getProfile().getNickname())
                && memberRepository.existsByNickname(dto.getNickname())) {
            log.warn("[updateMember] 중복된 닉네임으로 업데이트 시도: {}", dto.getNickname());
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 비밀번호 인코딩
        String encodedPassword = null;
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            encodedPassword = passwordEncoder.encode(dto.getPassword());
            log.debug("[updateMember] 비밀번호 인코딩 완료 - username={}, encodedPW={}", member.getUsername(), encodedPassword);
        }

        // 엔티티 업데이트
        member.update(encodedPassword, dto.getNickname(), dto.getImageUrl());
        log.info("[updateMember] 회원 정보 수정 완료 - ID={}, username={}", member.getId(), member.getUsername());

        MemberResponseDto responseDto = memberMapper.toDto(member);
        log.debug("[updateMember] 엔티티→DTO 변환 완료: {}", responseDto);
        return responseDto;
    }

    /**
     * ✅ 회원 삭제 (Soft Delete)
     */
    @Transactional
    public void deleteMember(Long id) {
        log.info("[MemberService.deleteMember] 회원 삭제 시도 - ID={}", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[deleteMember] 회원 조회 실패 - 존재하지 않는 ID={}", id);
                    return new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
                });
        member.softDelete("사용자 요청에 의한 삭제");
        log.warn("[deleteMember] 회원 소프트 삭제 완료 - ID={}, deletedAt={}", member.getId(), member.getStatusDetail().getDeletedAt());
    }
}
