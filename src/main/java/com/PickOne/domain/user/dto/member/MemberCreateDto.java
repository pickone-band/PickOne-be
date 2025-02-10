package com.PickOne.domain.user.dto.member;

import com.PickOne.domain.user.dto.profile.ProfileUpdateDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberCreateDto {
    private String username;
    private String password;
    private String email;
    private String nickname;
    private ProfileUpdateDto profile;
}
