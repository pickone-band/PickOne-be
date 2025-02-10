package com.PickOne.domain.user.dto.member;

import com.PickOne.domain.user.dto.aouthAccount.OAuthAccountResponseDto;
import com.PickOne.domain.user.dto.profile.ProfileResponseDto;
import com.PickOne.domain.user.model.MemberStatusDetail;
import com.PickOne.domain.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private Role role;  // Role 추가
    private ProfileResponseDto profile;
    private MemberStatusDetail statusDetail;
    private final List<OAuthAccountResponseDto> oauthAccounts;
}