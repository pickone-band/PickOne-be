package com.PickOne.global.security.details;

import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.MemberState;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SecurityMember {
    private final Long id;
    private final String loginId;
    private final String username;
    private final String email;
    private final String role;
    private final String status;

    public static SecurityMember from(Member member, MemberState memberState) {
        return SecurityMember.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .username(member.getUsername())
                .email(member.getEmail())
                .role(member.getRole().name())
                .status(memberState.getStatus().name())
                .build();
    }
}
