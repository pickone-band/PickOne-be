package com.PickOne.global.security.details;

import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.MemberState;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.domain.user.repository.MemberStateRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberStateRepository memberStateRepository;

    // ✅ Spring Security 인증용
    @Override
    public UserDetails loadUserByUsername(String username) {
        Member member = memberRepository.findByLoginId(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        MemberState memberState = memberStateRepository.findByMember(member)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        return new SecurityUserDetails(member, memberState);
    }

    // ✅ API 응답용
    public SecurityMember getUserDetails(String username) {
        Member member = memberRepository.findByLoginId(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        MemberState memberState = memberStateRepository.findByMember(member)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        return SecurityMember.from(member, memberState);
    }
}
