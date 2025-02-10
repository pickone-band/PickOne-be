package com.PickOne.domain.user.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "agreement_policies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public AgreementPolicy(String title, String content, Long Version) {
        this.title = title;
        this.content = content;
        this.version = version;
        this.isActive = true;
    }

    public AgreementPolicy deactivate() {
        return new AgreementPolicy(this.title, this.content, this.version, this.isRequired, false, this.startDate, this.endDate);
    }

    private AgreementPolicy(String title, String content, Long version, boolean isRequired, boolean isActive, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.content = content;
        this.version = version;
        this.isRequired = isRequired;
        this.isActive = isActive;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
