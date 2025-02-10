package com.PickOne.domain.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    public MemberStatusDetail ban(String reason) {
        return new MemberStatusDetail(MemberStatus.BANNED, LocalDateTime.now(), null, reason);
    }

    /** 회원 삭제 */
    public MemberStatusDetail delete(String reason) {
        return new MemberStatusDetail(MemberStatus.DELETED, null, LocalDateTime.now(), reason);
    }

    /** 회원 복구 */
    public MemberStatusDetail reactivate() {
        return new MemberStatusDetail(MemberStatus.ACTIVE, null, null, null);
    }

    private MemberStatusDetail(MemberStatus status, LocalDateTime bannedAt, LocalDateTime deletedAt, String reason) {
        this.status = status;
        this.bannedAt = bannedAt;
        this.deletedAt = deletedAt;
        this.reason = reason;
    }
}
