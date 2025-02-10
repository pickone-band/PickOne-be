package com.PickOne.domain.user.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ProfileResponseDto {
    private String name;
    private String imageUrl;
}