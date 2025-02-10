package com.PickOne.domain.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Embedded
    private Profile profile;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Embedded
    private MemberStatusDetail statusDetail;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "member_agreements", joinColumns = @JoinColumn(name = "member_id"))
    private List<MemberAgreement> agreements = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "oauth_accounts", joinColumns = @JoinColumn(name = "member_id"))
    private List<OAuthAccount> oauthAccounts = new ArrayList<>();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Member(String username, String email, String password, Profile profile, Role role, MemberStatusDetail statusDetail) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile != null ? profile : new Profile("", null);
        this.role = role != null ? role : Role.USER; // 기본 역할을 USER로 설정
        this.statusDetail = statusDetail != null ? statusDetail : new MemberStatusDetail(MemberStatus.ACTIVE);
    }

    public Member update(String username, String nickname, Profile profile, Role role, MemberStatusDetail statusDetail) {
        return new Member(
                username != null ? username : this.username,
                this.email,
                this.password,
                profile != null ? profile.update(profile.getName(), profile.getImageUrl()) : this.profile,
                role != null ? role : this.role,
                statusDetail != null ? statusDetail : this.statusDetail
        );
    }

    /** 약관 동의 */
    public void agreeToPolicy(AgreementPolicy policy) {
        this.agreements.add(new MemberAgreement(policy.getId(), policy.getVersion(), LocalDateTime.now()));
    }

    /** OAuth 계정 추가 */
    public void addOAuthAccount(OAuthAccount account) {
        this.oauthAccounts.add(account);
    }

    /** 프로필 업데이트 */
    public void updateProfile(String name, String imageUrl) {
        this.profile = new Profile(name, imageUrl);
    }

    /** 회원 정지 */
    public void ban(String reason) {
        this.statusDetail = this.statusDetail.ban(reason);
    }

    /** 소프트 삭제 */
    public void softDelete(String reason) {
        this.deletedAt = LocalDateTime.now();
        this.statusDetail = this.statusDetail.delete(reason);
    }

    /** 회원 복원 */
    public void restore() {
        this.deletedAt = null;
        this.statusDetail = this.statusDetail.reactivate();
    }

    /** 회원이 삭제되었는지 확인 */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
