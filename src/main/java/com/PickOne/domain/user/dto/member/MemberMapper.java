package com.PickOne.domain.user.dto.member;

import com.PickOne.domain.user.dto.aouthAccount.OAuthAccountMapper;
import com.PickOne.domain.user.dto.aouthAccount.OAuthAccountResponseDto;
import com.PickOne.domain.user.dto.profile.ProfileMapper;
import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.MemberStatus;
import com.PickOne.domain.user.model.Role;
import com.PickOne.domain.user.model.MemberStatusDetail;
import com.PickOne.global.util.mapper.BaseMapper;

import java.util.List;
import java.util.stream.Collectors;

public class MemberMapper implements BaseMapper<Member, MemberResponseDto> {

    private final ProfileMapper profileMapper = new ProfileMapper();
    private final OAuthAccountMapper oauthMapper = new OAuthAccountMapper();

    @Override
    public MemberResponseDto toDto(Member entity) {

        List<OAuthAccountResponseDto> oauthAccounts = entity.getOauthAccounts() != null
                ? entity.getOauthAccounts().stream()
                .map(oauthMapper::toDto)
                .collect(Collectors.toList())
                : List.of();

        return new MemberResponseDto(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getProfile().getName(),
                entity.getRole(),
                profileMapper.toDto(entity.getProfile()),
                entity.getStatusDetail(),
                oauthAccounts
        );
    }

    @Override
    public Member toEntity(MemberResponseDto dto) {
        return new Member(
                dto.getUsername(),
                dto.getEmail(),
                null, // 비밀번호는 별도 설정 필요
                profileMapper.toEntity(dto.getProfile()),
                dto.getRole() != null ? dto.getRole() : Role.USER, // 기본 역할 설정
                dto.getStatusDetail() != null ? dto.getStatusDetail() : new MemberStatusDetail(MemberStatus.ACTIVE)
        );
    }

    public Member toEntity(MemberCreateDto dto, String encodedPassword) {
        return new Member(
                dto.getUsername(),
                dto.getEmail(),
                encodedPassword,
                profileMapper.toEntity(dto.getProfile()),
                Role.USER, // 기본 역할 USER
                new MemberStatusDetail(MemberStatus.ACTIVE) // 기본 상태 설정
        );
    }

    public Member updateEntity(Member member, MemberUpdateDto dto) {
        return member.update(
                dto.getUsername(),
                dto.getNickname(),
                dto.getProfile() != null ? profileMapper.toEntity(dto.getProfile()) : member.getProfile(),
                dto.getRole() != null ? dto.getRole() : member.getRole(),
                dto.getStatusDetail() != null ? dto.getStatusDetail() : member.getStatusDetail()
        );
    }
}
