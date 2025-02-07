package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.SocialAccount;
import com.PickOne.domain.user.model.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    boolean existsByMemberIdAndProvider(Long memberId, SocialProvider provider); // 소셜 계정 중복

    Optional<SocialAccount> findByProviderAndProviderUserId(SocialProvider socialProvider, String providerUserId);
}
