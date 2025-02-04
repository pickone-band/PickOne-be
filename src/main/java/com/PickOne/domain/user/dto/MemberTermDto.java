package com.PickOne.domain.user.dto;

import lombok.*;

public class MemberTermDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberTermCreateDto {
        private Long memberId;
        private Long termId;
        private Boolean isAgreed;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberTermUpdateDto {
        private Boolean isAgreed;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberTermResponseDto {
        private Long id;
        private Long memberId;
        private Long termId;
        private Boolean isAgreed;
    }
}
