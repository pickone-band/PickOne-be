package com.PickOne.global.security.details;

import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.MemberState;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.domain.user.repository.MemberStateRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private MemberRepository memberRepository;
    private MemberStateRepository memberStateRepository;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        memberStateRepository = mock(MemberStateRepository.class);
        customUserDetailsService = new CustomUserDetailsService(memberRepository, memberStateRepository);
    }

    @Test
    void loadUserByUsername_ValidUser_ReturnsUserDetails() {
        // Given
        String username = "testUser";
        Member member = Member.builder().loginId(username).build();
        MemberState memberState = MemberState.builder().member(member).build();

        when(memberRepository.findByLoginId(username)).thenReturn(Optional.of(member));
        when(memberStateRepository.findByMember(member)).thenReturn(Optional.of(memberState));

        // When
        SecurityUserDetails result = (SecurityUserDetails) customUserDetailsService.loadUserByUsername(username);

        // Then
        assertNotNull(result);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // Given
        String username = "nonExistentUser";

        when(memberRepository.findByLoginId(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessException.class, () -> customUserDetailsService.loadUserByUsername(username));
    }
}
