package com.PickOne.domain.user.dto;

import lombok.*;

import java.time.LocalDate;

public class ProfileDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProfileUpdateDto {
        private String phoneNumber;
        private LocalDate birthDate;
        private String profilePicUrl;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProfileResponseDto {
        private Long id;
        private Long memberId;
        private String phoneNumber;
        private LocalDate birthDate;
        private String profilePicUrl;
    }

}
