package com.PickOne.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class TermDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TermCreateDto {

        @NotBlank
        @Size(max = 255)
        private String title;

        @NotBlank
        private String content;

        @NotNull
        @Size(min = 1, max = 10)
        private String version;

        @NotNull
        private Boolean isRequired; // 필수 여부

        @NotNull
        private Boolean isActive; // 활성 여부

        @NotNull
        private LocalDate startDate;

        private LocalDate endDate; // 무기한 유효, 종료 날짜가 정해지지 않았을 때 null

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TermResponseDto {
        private Long id;
        private String title;
        private String content;
        private String version;
        private Boolean isRequired;
        private Boolean isActive;
        private LocalDate startDate;
        private LocalDate endDate;
    }


}


