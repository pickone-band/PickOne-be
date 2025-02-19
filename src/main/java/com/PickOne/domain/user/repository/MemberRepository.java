package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryDslRepository {

    Optional<Member> findById(Long id);

    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.profile.nickname = :nickname")
    boolean existsByNickname(@Param("nickname") String nickname);
}
