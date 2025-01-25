package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface TermRepository extends JpaRepository<Term, Long> {
    // 약관 버전 중복 확인
    boolean existsByTitleAndVersion(String title, String version); //
}
