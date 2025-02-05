package com.PickOne.domain.user.dto;

import com.PickOne.domain.user.model.SocialProvider;
import lombok.Getter;
import lombok.Setter;

public class SocialAccountDto {

    @Getter
    @Setter
    public static class SocialAccountCreateDto {
        private Long memberId;
        private SocialProvider provider;
        private String providerUserId;
    }

    @Getter
    @Setter
    public static class SocialAccountUpdateDto {
        private SocialProvider provider;
        private String providerUserId;
    }

    @Getter @Setter
    public static class SocialAccountResponseDto {
        private Long id;
        private Long memberId;
        private SocialProvider provider;
        private String providerUserId;
    }
}
