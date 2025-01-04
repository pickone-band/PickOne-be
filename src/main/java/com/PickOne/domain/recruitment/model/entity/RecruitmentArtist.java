package com.PickOne.domain.recruitment.model.entity;

import com.PickOne.domain.preference.model.entity.Artist;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
@Entity
public class RecruitmentArtist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recruitmentArtistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", nullable = false)
    private Recruitment recruitment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist; // Enum 사용
}
