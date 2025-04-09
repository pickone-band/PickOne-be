package com.PickOne.domain.user.dto.member;

import com.PickOne.domain.user.dto.profile.ProfileUpdateDto;
import com.PickOne.domain.user.model.AgreementPolicy;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberCreateDto {
    private String username;
    private String password;
    private String email;
    private String nickname;
    private List<Long> agreementPolicyIds;
}
