package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.QMember;
import com.PickOne.domain.user.model.QMemberStatusDetail;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberQueryDslRepositoryImpl implements MemberQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findByDynamicCondition(Long id, String username, String email, String role) {
        log.info("[MemberQueryDslRepositoryImpl.findByDynamicCondition] 회원 단일 조회 시작. id={}, username={}, email={}, role={}",
                id, username, email, role);

        QMember qMember = QMember.member;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(qMember.id.eq(id));
            log.debug("[findByDynamicCondition] 조건 추가: id={}", id);
        }
        if (username != null) {
            builder.and(qMember.username.eq(username));
            log.debug("[findByDynamicCondition] 조건 추가: username={}", username);
        }
        if (email != null) {
            builder.and(qMember.email.eq(email));
            log.debug("[findByDynamicCondition] 조건 추가: email={}", email);
        }
        if (role != null) {
            builder.and(qMember.role.stringValue().eq(role));
            log.debug("[findByDynamicCondition] 조건 추가: role={}", role);
        }

        Member result = queryFactory
                .selectFrom(qMember)
                .where(builder)
                .fetchOne();

        if (result == null) {
            log.warn("[findByDynamicCondition] 조회 결과 없음.");
        } else {
            log.info("[findByDynamicCondition] 조회 성공. memberID={}, username={}", result.getId(), result.getUsername());
        }
        return Optional.ofNullable(result);
    }

    @Override
    public Page<Member> searchMembers(String username, String email, String role, Pageable pageable) {
        log.info("[MemberQueryDslRepositoryImpl.searchMembers] 회원 검색 (페이징) 시작. username={}, email={}, role={}, pageable={}",
                username, email, role, pageable);

        QMember qMember = QMember.member;
        BooleanBuilder builder = new BooleanBuilder();

        if (username != null) {
            builder.and(qMember.username.containsIgnoreCase(username));
            log.debug("[searchMembers] 조건 추가: username.containsIgnoreCase({})", username);
        }
        if (email != null) {
            builder.and(qMember.email.containsIgnoreCase(email));
            log.debug("[searchMembers] 조건 추가: email.containsIgnoreCase({})", email);
        }
        if (role != null) {
            builder.and(qMember.role.stringValue().eq(role));
            log.debug("[searchMembers] 조건 추가: role={}", role);
        }

        List<Member> content = queryFactory
                .selectFrom(qMember)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(qMember)
                .where(builder)
                .fetchCount();

        log.info("[searchMembers] 조회 완료. content.size()={}, total={}", content.size(), total);
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<Member> findDeletedBeforeThreshold() {
        log.info("[MemberQueryDslRepositoryImpl.findDeletedBeforeThreshold] 소프트 삭제 회원 조회 (1개월 이전) 시작.");

        QMember qMember = QMember.member;
        QMemberStatusDetail qStatusDetail = qMember.statusDetail;

        LocalDateTime threshold = LocalDateTime.now().minusMonths(1);
        log.debug("[findDeletedBeforeThreshold] threshold={}", threshold);

        List<Member> deletedMembers = queryFactory
                .selectFrom(qMember)
                .where(qStatusDetail.deletedAt.isNotNull()
                        .and(qStatusDetail.deletedAt.before(threshold)))
                .fetch();

        log.info("[findDeletedBeforeThreshold] 조회 완료. deletedMembers.size()={}", deletedMembers.size());
        return deletedMembers;
    }
}
