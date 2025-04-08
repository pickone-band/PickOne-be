package com.PickOne.domain.user.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Profile {

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "image_url")
    private String imageUrl;

    public Profile(String nickname, String imageUrl) {
        log.debug("[Profile 생성자] nickname={}, imageUrl={}", nickname, imageUrl);
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }

    public Profile update(String nickname, String imageUrl) {
        log.debug("[Profile.update] 프로필 업데이트 (기존 nickname={}, 기존 imageUrl={})",
                this.nickname, this.imageUrl);
        return new Profile(
                nickname != null ? nickname : this.nickname,
                imageUrl != null ? imageUrl : this.imageUrl
        );
    }
}
