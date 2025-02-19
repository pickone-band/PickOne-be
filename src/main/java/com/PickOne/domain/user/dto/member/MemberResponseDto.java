package com.PickOne.domain.user.dto.member;

import com.PickOne.domain.user.dto.aouthAccount.OAuthAccountResponseDto;
import com.PickOne.domain.user.dto.profile.ProfileResponseDto;
import com.PickOne.domain.user.model.MemberStatusDetail;
import com.PickOne.domain.user.model.Role;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {

    private Long id;
    private String username;
    private String email;
    private String nickname;
    private Role role;
    private String status;
    private List<OAuthAccountResponseDto> oauthAccounts;

    }
