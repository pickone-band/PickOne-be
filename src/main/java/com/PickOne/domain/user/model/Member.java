package com.PickOne.domain.user.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
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

    // 정적 팩토리 메서드
    public static Member create(String username, String email, String encodedPassword,
                                String nickname, List<AgreementPolicy> agreementPolicies) {
        log.info("[Member.create] 회원 엔티티 생성 시도(username={}, email={})", username, email);
        Profile profile = new Profile(nickname, null);
        Member member = new Member(username, email, encodedPassword, profile, Role.USER,
                new MemberStatusDetail(MemberStatus.ACTIVE));
        member.addAgreements(agreementPolicies);
        log.info("[Member.create] 회원 엔티티 생성 완료: {}", member);
        return member;
    }

    public Member(String username, String email, String password,
                  Profile profile, Role role, MemberStatusDetail statusDetail) {
        log.info("[Member 생성자] username={}, email={}", username, email);
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile != null ? profile : new Profile("", null);
        this.role = role != null ? role : Role.USER;
        this.statusDetail = statusDetail != null ? statusDetail : new MemberStatusDetail(MemberStatus.ACTIVE);
    }

    /** 비밀번호 및 프로필(닉네임, 이미지) 업데이트 */
    public Member update(String newPassword, String nickname, String imageUrl) {
        log.info("[Member.update] 회원 정보 수정 시도 (ID={}, username={})", this.id, this.username);
        // 비밀번호 업데이트
        if (newPassword != null && !newPassword.isEmpty()) {
            this.password = newPassword;
            log.debug("[Member.update] 비밀번호 변경 완료");
        }
        // 프로필 닉네임 및 이미지 업데이트
        if (nickname != null || imageUrl != null) {
            this.profile = this.profile.update(
                    nickname != null ? nickname : this.profile.getNickname(),
                    imageUrl != null ? imageUrl : this.profile.getImageUrl()
            );
            log.debug("[Member.update] 프로필 변경 완료: nickname={}, imageUrl={}", nickname, imageUrl);
        }
        log.info("[Member.update] 회원 정보 수정 완료 (ID={}, username={})", this.id, this.username);
        return this;
    }

    /** 약관 동의 */
    public void agreeToPolicy(AgreementPolicy policy) {
        log.debug("[Member.agreeToPolicy] 약관 동의 (memberID={}, policyID={})", this.id, policy.getId());
        this.agreements.add(new MemberAgreement(policy.getId(), LocalDateTime.now()));
    }

    /** 여러 약관 동의 추가 */
    public void addAgreements(List<AgreementPolicy> agreementPolicies) {
        if (agreementPolicies == null || agreementPolicies.isEmpty()) {
            log.debug("[Member.addAgreements] 동의할 약관이 없음");
            return;
        }
        log.info("[Member.addAgreements] 약관 동의 시도 (memberID={}, agreementCount={})", this.id, agreementPolicies.size());
        for (AgreementPolicy policy : agreementPolicies) {
            boolean alreadyAgreed = this.agreements.stream()
                    .anyMatch(agreement -> Objects.equals(agreement.getPolicyId(), policy.getId()));
            if (!alreadyAgreed) {
                agreeToPolicy(policy);
            } else {
                log.debug("[Member.addAgreements] 이미 동의한 약관 (policyID={})", policy.getId());
            }
        }
        log.info("[Member.addAgreements] 약관 동의 처리 완료 (memberID={})", this.id);
    }

    /** OAuth 계정 추가 */
    public void addOAuthAccount(OAuthAccount account) {
        log.debug("[Member.addOAuthAccount] OAuth 계정 추가 시도 (memberID={}, provider={})",
                this.id, account.getProvider());
        this.oauthAccounts.add(account);
        log.info("[Member.addOAuthAccount] OAuth 계정 추가 완료 (memberID={})", this.id);
    }

    /** 프로필 업데이트 */
    public void updateProfile(String name, String imageUrl) {
        log.debug("[Member.updateProfile] 프로필 업데이트 (memberID={}, name={}, imageUrl={})", this.id, name, imageUrl);
        this.profile = new Profile(name, imageUrl);
        log.info("[Member.updateProfile] 프로필 업데이트 완료 (memberID={})", this.id);
    }

    /** 회원 정지 */
    public void ban(String reason) {
        log.warn("[Member.ban] 회원 정지 시도 (memberID={}, reason={})", this.id, reason);
        this.statusDetail.ban(reason);
        log.warn("[Member.ban] 회원 정지 완료 (memberID={}, reason={})", this.id, reason);
    }

    /** 소프트 삭제 */
    public void softDelete(String reason) {
        log.warn("[Member.softDelete] 회원 소프트 삭제 시도 (memberID={}, reason={})", this.id, reason);
        this.statusDetail.delete(reason);
        log.warn("[Member.softDelete] 회원 소프트 삭제 완료 (memberID={}, deletedAt={})", this.id, this.statusDetail.getDeletedAt());
    }

    /** 회원 복원 */
    public void restore() {
        log.info("[Member.restore] 회원 복원 시도 (memberID={})", this.id);
        this.statusDetail.reactivate();
        log.info("[Member.restore] 회원 복원 완료 (memberID={})", this.id);
    }

    /** 회원이 삭제되었는지 확인 */
    public boolean isDeleted() {
        return this.statusDetail.getDeletedAt() != null;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", profile=" + (profile != null ? profile.getNickname() : "null") +
                ", role=" + role +
                '}';
    }
}
