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
        this.status = status;
    }

    /** 회원 정지 */
    public void ban(String reason) {
        log.debug("[MemberStatusDetail.ban] 회원 정지 처리 (reason={})", reason);
        this.status = MemberStatus.BANNED;
        this.bannedAt = LocalDateTime.now();
        this.reason = reason;
    }

    /** 회원 삭제 */
    public void delete(String reason) {
        log.debug("[MemberStatusDetail.delete] 회원 삭제 처리 (reason={})", reason);
        this.status = MemberStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
        this.reason = reason;
    }

    /** 회원 복구 */
    public void reactivate() {
        log.debug("[MemberStatusDetail.reactivate] 회원 복원 처리");
        this.status = MemberStatus.ACTIVE;
        this.bannedAt = null;
        this.deletedAt = null;
        this.reason = null;
    }
}

