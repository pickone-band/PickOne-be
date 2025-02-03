package com.PickOne.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class ProfileDto {

    @Getter
    @Setter
    public static class ProfileCreateDto {
        private Long memberId;
        private String phoneNumber;
        private LocalDate birthDate;
        private String profilePicUrl;
    }

    @Getter
    @Setter
    public static class ProfileUpdateDto {
        private String phoneNumber;
        private LocalDate birthDate;
        private String profilePicUrl;
    }

    @Getter
    @Setter
    public static class ProfileResponseDto {
        private Long id;
        private Long memberId;
        private String phoneNumber;
        private LocalDate birthDate;
        private String profilePicUrl;
    }

}
