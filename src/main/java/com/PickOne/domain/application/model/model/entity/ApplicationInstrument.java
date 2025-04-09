package com.PickOne.domain.application.model.model.entity;

import com.PickOne.domain.preference.model.entity.Instrument;
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
public class ApplicationInstrument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationInstrumentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING) // Enum 필드에는 @Enumerated를 사용
    private Instrument instrument;
}

