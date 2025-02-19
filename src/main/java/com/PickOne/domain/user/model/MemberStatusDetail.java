package com.PickOne.domain.user.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class MemberStatusDetail {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status;

    @Column(name = "banned_at")
    private LocalDateTime bannedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "reason")
    private String reason;

    public MemberStatusDetail(MemberStatus status) {
        log.debug("[MemberStatusDetail 생성자] status={}", status);
        this.status = status;
    }

    /** 회원 정지 */
    public MemberStatusDetail ban(String reason) {
        log.debug("[MemberStatusDetail.ban] 회원 정지 처리 (reason={})", reason);
        return new MemberStatusDetail(MemberStatus.BANNED, LocalDateTime.now(), null, reason);
    }

    /** 회원 삭제 */
    public MemberStatusDetail delete(String reason) {
        log.debug("[MemberStatusDetail.delete] 회원 삭제 처리 (reason={})", reason);
        return new MemberStatusDetail(MemberStatus.DELETED, null, LocalDateTime.now(), reason);
    }

    /** 회원 복구 */
    public MemberStatusDetail reactivate() {
        log.debug("[MemberStatusDetail.reactivate] 회원 복원 처리");
        return new MemberStatusDetail(MemberStatus.ACTIVE, null, null, null);
    }

    private MemberStatusDetail(MemberStatus status, LocalDateTime bannedAt,
                               LocalDateTime deletedAt, String reason) {
        this.status = status;
        this.bannedAt = bannedAt;
        this.deletedAt = deletedAt;
        this.reason = reason;
    }
}
