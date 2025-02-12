package com.PickOne.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class MemberStateDto {

    @Getter
    @Setter
    public static class MemberStateUpdateDto {
        private Long id;
        private String status;
        private LocalDateTime bannedAt;
        private LocalDateTime deletedAt;
        private String reason;
    }

    @Getter
    @Setter
    public static class MemberStateResponseDto {
        private Long id;
        private Long memberId;
        private String status;
        private LocalDateTime bannedAt;
        private LocalDateTime deletedAt;
        private String reason;
        // createdAt, updatedAt 필요하면 추가
    }
}
