package com.PickOne.domain.user.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    USER,
    ADMIN; // 일반, 관리자

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }

}
