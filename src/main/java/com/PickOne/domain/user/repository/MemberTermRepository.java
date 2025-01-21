package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.MemberTerm;
import com.PickOne.domain.user.model.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {

    boolean existsByMemberAndTerm(Member member, Term term);
    List<MemberTerm> findAllByMember(Member member);

}
