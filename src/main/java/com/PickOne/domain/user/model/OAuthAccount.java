package com.PickOne.domain.user.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class OAuthAccount {

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private SocialProvider provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "social_email")
    private String email;

    @Column(name = "social_image_url")
    private String profileImage;

    @Column(name = "social_name")
    private String name;

    public OAuthAccount(SocialProvider provider, String providerId,
                        String email, String profileImage, String name) {
        log.debug("[OAuthAccount 생성자] provider={}, providerId={}", provider, providerId);
        this.provider = provider;
        this.providerId = providerId;
        this.email = email;
        this.profileImage = profileImage;
        this.name = name;
    }
}
