package com.PickOne.domain.user.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Entity
@Table(name = "agreement_policies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class AgreementPolicy extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean isRequired;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    public AgreementPolicy(String title, String content, Long version) {
        log.info("[AgreementPolicy 생성자] 약관 엔티티 생성(title={}, version={})", title, version);
        this.title = title;
        this.content = content;
        this.version = version;
        this.isActive = true;
    }

    // 정적 팩토리 메서드
    public static AgreementPolicy create(String title, String content, Long version,
                                         boolean isRequired, boolean isActive,
                                         LocalDate startDate, LocalDate endDate) {
        log.info("[AgreementPolicy.create] 약관 생성 시도 (title={}, version={}, isRequired={}, isActive={})",
                title, version, isRequired, isActive);
        AgreementPolicy policy = new AgreementPolicy();
        policy.title = title;
        policy.content = content;
        policy.version = version;
        policy.isRequired = isRequired;
        policy.isActive = isActive;
        policy.startDate = startDate;
        policy.endDate = endDate;
        log.info("[AgreementPolicy.create] 약관 생성 완료: {}", policy);
        return policy;
    }

    private AgreementPolicy(String title, String content, Long version,
                            boolean isRequired, boolean isActive,
                            LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.content = content;
        this.version = version;
        this.isRequired = isRequired;
        this.isActive = isActive;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AgreementPolicy deactivate() {
        log.info("[AgreementPolicy.deactivate] 약관 비활성화 시도: {}", this.title);
        return new AgreementPolicy(
                this.title,
                this.content,
                this.version,
                this.isRequired,
                false,
                this.startDate,
                this.endDate
        );
    }

    @Override
    public String toString() {
        return "AgreementPolicy{" +
                "id=" + id +
                ", version=" + version +
                ", title='" + title + '\'' +
                ", isRequired=" + isRequired +
                ", isActive=" + isActive +
                '}';
    }
}
