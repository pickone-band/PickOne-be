package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.MemberState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberStateRepository extends JpaRepository<MemberState, Long> {
}
