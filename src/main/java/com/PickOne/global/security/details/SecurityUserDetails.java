package com.PickOne.global.security.details;

import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.MemberState;
import com.PickOne.domain.user.model.MemberStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class SecurityUserDetails implements UserDetails {

    @Getter
    private final Member member;
    private final MemberState memberState;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_" + member.getRole().name());  // ex) ROLE_USER, ROLE_ADMIN
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return memberState.getStatus() != MemberStatus.DELETED;  // 탈퇴한 계정이면 false
    }

    @Override
    public boolean isAccountNonLocked() {
        return memberState.getStatus() != MemberStatus.BANNED;  // 정지된 계정이면 false
    }

    @Override
    public boolean isEnabled() {
        return memberState.getStatus() == MemberStatus.ACTIVE;  // 활성화된 사용자만 로그인 가능
    }

}
