package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.MemberStateDto.*;
import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.MemberState;
import com.PickOne.domain.user.model.MemberStatus;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.domain.user.repository.MemberStateRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberStateService {

    private final MemberRepository memberRepository;
    private final MemberStateRepository memberStateRepository;
    private final ModelMapper modelMapper;

    /** 특정 회원의 상태 변경 (ex: BAN, ACTIVE) */
    public MemberStateResponseDto updateMemberState(MemberStateUpdateDto dto) {
        MemberState state = memberStateRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        MemberStatus newStatus = MemberStatus.valueOf(dto.getStatus());
        if (newStatus == MemberStatus.BANNED) {
            if (state.getStatus() == MemberStatus.BANNED) {
                throw new BusinessException(ErrorCode.ALREADY_BANNED);
            }
            state = MemberState.builder()
                    .id(state.getId())
                    .member(state.getMember())
                    .status(newStatus)
                    .bannedAt(LocalDateTime.now())
                    .reason(dto.getReason())
                    .build();
        } else if (newStatus == MemberStatus.ACTIVE) {
            if (state.getStatus() == MemberStatus.ACTIVE) {
                throw new BusinessException(ErrorCode.ALREADY_ACTIVE);
            }
            state = MemberState.builder()
                    .id(state.getId())
                    .member(state.getMember())
                    .status(newStatus)
                    .bannedAt(null)
                    .reason(null)
                    .build();
        }
        memberStateRepository.save(state);
        return modelMapper.map(state, MemberStateResponseDto.class);
    }

    @Transactional(readOnly = true)
    public MemberStateResponseDto getMemberState(Long id) {
        MemberState ms = memberStateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return modelMapper.map(ms, MemberStateResponseDto.class);
    }

    @Transactional(readOnly = true)
    public List<MemberStateResponseDto> getAllStates() {
        return memberStateRepository.findAll().stream()
                .map(ms -> modelMapper.map(ms, MemberStateResponseDto.class))
                .collect(Collectors.toList());
    }
}
