package com.PickOne.domain.user.dto.profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateDto {
    private String nickname;
    private String imageUrl;
}
