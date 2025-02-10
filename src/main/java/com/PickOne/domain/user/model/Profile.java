package com.PickOne.domain.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    public Profile(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public Profile update(String name, String imageUrl) {
        return new Profile(
                name != null ? name : this.name,
                imageUrl != null ? imageUrl : this.imageUrl
        );
    }

}

