package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberQueryDslRepository {

    // 조건부 회원 조회 (단일 회원)
    Optional<Member> findByDynamicCondition(Long id, String username, String email, String role);

    // 페이징을 지원하는 검색 기능
    Page<Member> searchMembers(String username, String email, String role, Pageable pageable);

    // 일정 기간이 지난 소프트 삭제 회원 검색
    List<Member> findDeletedBeforeThreshold();
}
