package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.MemberState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberStateRepository extends JpaRepository<MemberState, Long> {
    Optional<MemberState> findByMember(Member member);
}
