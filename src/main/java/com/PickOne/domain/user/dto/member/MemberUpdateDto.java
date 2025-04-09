package com.PickOne.domain.user.dto.member;

import com.PickOne.domain.user.dto.profile.ProfileUpdateDto;
import com.PickOne.domain.user.model.MemberStatusDetail;
import com.PickOne.domain.user.model.Role;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDto {

    private String nickname;
    private String imageUrl;
    private String password;

}