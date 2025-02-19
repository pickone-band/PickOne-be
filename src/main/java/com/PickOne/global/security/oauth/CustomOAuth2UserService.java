package com.PickOne.global.security.oauth;

import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.Role;
import com.PickOne.domain.user.model.SocialAccount;
import com.PickOne.domain.user.model.SocialProvider;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.domain.user.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerUserId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String nickname = name != null ? name : "DefaultNickname"; // 기본 닉네임 설정

        // 이메일로 기존 사용자 찾기
        Optional<Member> existingMember = memberRepository.findByEmail(email);

        if (existingMember.isPresent()) {
            Member member = existingMember.get();

            // 소셜 계정이 연동되어 있는지 확인
            Optional<SocialAccount> linkedSocialAccount = socialAccountRepository.findByProviderAndProviderUserId(
                    SocialProvider.valueOf(provider.toUpperCase()), providerUserId);

            if (linkedSocialAccount.isEmpty()) {
                // 소셜 계정 연결
                SocialAccount socialAccount = SocialAccount.builder()
                        .member(member)
                        .provider(SocialProvider.valueOf(provider.toUpperCase()))
                        .providerUserId(providerUserId)
                        .build();

                socialAccountRepository.save(socialAccount);
            }

            return new CustomOAuth2User(oauth2User); // 기존 사용자 반환
        }

        // 새로운 사용자 생성
        Member newMember = Member.builder()
                .loginId(email)
                .username(name)
                .email(email)
                .nickname(nickname) // 기본 닉네임 설정
                .role(Role.USER)
                .build();

        memberRepository.save(newMember);

        // 소셜 계정 연결
        SocialAccount socialAccount = SocialAccount.builder()
                .member(newMember)
                .provider(SocialProvider.valueOf(provider.toUpperCase()))
                .providerUserId(providerUserId)
                .build();

        socialAccountRepository.save(socialAccount);

        return new CustomOAuth2User(oauth2User); // 신규 사용자 반환
    }
}
