package com.PickOne.domain.application.model.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    // Preference와 UserInstrument의 1:N 관계
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationInstrument> recruitmentInstruments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // 상태 (예: 대기, 승인, 거절 등)

    private String selfIntroduction; // 자기소개

    private String videoLink; // 영상 링크

    private String documentLink; // 서류 링크

    private String finalComment; // 최종 한마디

}
