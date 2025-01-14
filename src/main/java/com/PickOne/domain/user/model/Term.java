package com.PickOne.domain.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Setter
@Table(name = "terms")
public class Term extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "terms_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "version", nullable = false, length = 10)
    private String version;

    @Column(name = "is_required", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isRequired;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isActive;

    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MemberTerm> memberTerms;
}
