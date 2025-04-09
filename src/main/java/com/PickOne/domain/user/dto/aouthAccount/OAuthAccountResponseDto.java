package com.PickOne.domain.user.dto.aouthAccount;

import com.PickOne.domain.user.model.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthAccountResponseDto {
    private final SocialProvider provider;
    private final String providerId;
    private final String email;
    private final String profileImage;
    private final String name;
}
