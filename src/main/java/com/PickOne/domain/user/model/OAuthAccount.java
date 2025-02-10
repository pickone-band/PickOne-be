package com.PickOne.domain.user.model;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthAccount {

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private SocialProvider provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "social_email", nullable = true)
    private String email;

    @Column(name = "social_image_url", nullable = true)
    private String profileImage;

    @Column(name = "social_name", nullable = true)
    private String name;

    public OAuthAccount(SocialProvider provider, String providerId, String email, String profileImage, String name) {
        this.provider = provider;
        this.providerId = providerId;
        this.email = email;
        this.profileImage = profileImage;
        this.name = name;
    }

}
