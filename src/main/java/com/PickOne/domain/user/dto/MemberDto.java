package com.PickOne.domain.user.dto;


import com.PickOne.domain.user.model.Role;
import lombok.NoArgsConstructor;


import lombok.Getter;
import lombok.Setter;

public class MemberDto {

    /**
     * (1) Create DTO
     */
    @NoArgsConstructor
    @Getter
    @Setter
    public static class MemberCreateDto {
        private String loginId;
        private String password;
        private String username;
        private String email;
        private String nickname;
    }

    /**
     * (2) Update DTO
     */
    @NoArgsConstructor
    @Getter
    @Setter
    public static class MemberUpdateDto {
        private String username;
        private String nickname;
    }

    /**
     * (3) Response DTO
     */
    @NoArgsConstructor
    @Getter
    @Setter
    public static class MemberResponseDto {
        private Long id;
        private String loginId;
        private String username;
        private String email;
        private String nickname;
        private Role role;
    }
}


