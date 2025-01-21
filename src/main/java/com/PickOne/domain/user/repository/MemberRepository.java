package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long id);
    Optional<Member> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId); // 아이디 중복
    boolean existsByEmail(String email); // 이메일 중복
    boolean existsByNickname(String nickname); // 닉네임 중복
}
