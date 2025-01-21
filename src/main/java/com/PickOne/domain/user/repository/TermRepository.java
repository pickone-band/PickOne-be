package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {

    // 필수 + 활성인 약관 목록
    List<Term> findByIsRequiredTrueAndIsActiveTrue();

    // 약관 버전 중복 확인
    boolean existsByVersion(String version); //
}
