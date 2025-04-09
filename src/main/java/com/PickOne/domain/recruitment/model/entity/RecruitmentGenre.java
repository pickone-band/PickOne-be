package com.PickOne.domain.recruitment.model.entity;

import com.PickOne.domain.preference.model.entity.Genre;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RecruitmentGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recruitmentGenreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", nullable = false)
    private Recruitment recruitment;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;
}
