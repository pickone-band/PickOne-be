package com.PickOne.domain.user.model;

public enum Role {

    USER, ADMIN; // 일반, 관리자

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
