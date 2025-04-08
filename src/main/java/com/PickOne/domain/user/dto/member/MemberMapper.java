package com.PickOne.domain.user.dto.member;

import com.PickOne.domain.user.dto.aouthAccount.OAuthAccountMapper;
import com.PickOne.domain.user.dto.aouthAccount.OAuthAccountResponseDto;
import com.PickOne.domain.user.model.*;
import com.PickOne.global.util.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberMapper implements BaseMapper<Member, MemberResponseDto> {

    private final OAuthAccountMapper oauthMapper = new OAuthAccountMapper();

    @Override
    public MemberResponseDto toDto(Member entity) {

        List<OAuthAccountResponseDto> oauthAccounts = entity.getOauthAccounts() != null
                ? entity.getOauthAccounts().stream()
                .map(oauthMapper::toDto)
                .collect(Collectors.toList())
                : List.of();

        return MemberResponseDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .nickname(entity.getProfile().getNickname())
                .role(entity.getRole())
                .status(entity.getStatusDetail().getStatus().name())
                .oauthAccounts(oauthAccounts)
                .build();
    }

    @Override
    public Member toEntity(MemberResponseDto dto) {
        Profile profile = new Profile(dto.getNickname(), null); // Profile 생성 시 imageUrl은 null

        return new Member(
                dto.getUsername(),
                dto.getEmail(),
                null, // 비밀번호는 별도로 설정
                profile,
                dto.getRole() != null ? dto.getRole() : Role.USER,
                new MemberStatusDetail(MemberStatus.valueOf(dto.getStatus()))
        );
    }

    public Member toEntity(MemberCreateDto dto, String encodedPassword, List<AgreementPolicy> agreementPolicies) {
        return Member.create(
                dto.getUsername(),
                dto.getEmail(),
                encodedPassword,
                dto.getNickname(),
                agreementPolicies
        );
    }

    public Member updateEntity(Member member, MemberUpdateDto dto) {
        return member.update(
                dto.getPassword(),    // ✅ 비밀번호
                dto.getNickname(),    // ✅ 닉네임
                dto.getImageUrl()
        );
    }
}
