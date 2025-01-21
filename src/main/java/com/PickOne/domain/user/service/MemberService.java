package com.PickOne.domain.user.service;


import com.PickOne.domain.user.dto.MemberDto.*;
import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.Role;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public void createMember(MemberCreateDto dto) {
        validateDuplicate(dto);

        Member member = modelMapper.map(dto, Member.class);
        member.setRole(Role.USER);
        member.setPassword(passwordEncoder.encode(dto.getPassword()));

        Member saved = memberRepository.save(member);
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


