package com.PickOne.domain.user.dto.member;

import com.PickOne.domain.user.dto.profile.ProfileUpdateDto;
import com.PickOne.domain.user.model.MemberStatusDetail;
import com.PickOne.domain.user.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateDto {
    private String username;
    private String nickname;
    private ProfileUpdateDto profile;
    private Role role;  // Role 추가
    private MemberStatusDetail statusDetail;
}