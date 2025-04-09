package com.PickOne.domain.preference.model;

import com.PickOne.domain.preference.model.entity.Artist;
import com.PickOne.domain.preference.model.entity.Preference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class UserArtist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userArtistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preference_id", nullable = false)
    private Preference preference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist; // Enum 사용
}
