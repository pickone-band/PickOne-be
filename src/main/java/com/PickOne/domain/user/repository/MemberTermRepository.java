package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.MemberTerm;
import com.PickOne.domain.user.model.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
    // 회원과 약관으로 조회
    Optional<MemberTerm> findByMemberAndTerm(Member member, Term term);

    // 특정 회원이 동의한 약관 목록 조회
    List<MemberTerm> findByMemberId(Long memberId);

    // 특정 약관에 동의한 회원 목록 조회
    List<MemberTerm> findByTermId(Long termId);

    // 회원 ID와 약관 ID로 조회 (커스텀 쿼리)
    @Query("SELECT mt FROM MemberTerm mt WHERE mt.member.id = :memberId AND mt.term.id = :termId")
    Optional<MemberTerm> findByMemberIdAndTermId(@Param("memberId") Long memberId, @Param("termId") Long termId);
}
