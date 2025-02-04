package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    boolean existsByPhoneNumber(String phoneNumber); // 휴대폰 중복

    Optional<Profile> findByMemberId(Long memberId);
}
