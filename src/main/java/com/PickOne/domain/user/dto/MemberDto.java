package com.PickOne.domain.user.dto;


import com.PickOne.domain.user.model.Role;
import lombok.*;


import java.util.List;
import com.PickOne.domain.user.dto.MemberTermDto.*;

public class MemberDto {

    /**
     * (1) Create DTO
     */

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MemberCreateDto {
        private String loginId;
        private String password;
        private String username;
        private String email;
        private String nickname;
        private List<MemberTermCreateDto> termAgreements;
    }

    /**
     * (2) Update DTO
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MemberUpdateDto {
        private String username;
        private String nickname;
    }

    /**
     * (3) Response DTO
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MemberResponseDto {
        private Long id;
        private String loginId;
        private String username;
        private String email;
        private String nickname;
        private Role role;
    }
}


