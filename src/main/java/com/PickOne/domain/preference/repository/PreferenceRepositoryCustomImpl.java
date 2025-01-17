package com.PickOne.domain.preference.repository;

import com.PickOne.domain.preference.dto.response.GenreDto;
import com.PickOne.domain.preference.dto.response.InstrumentDto;
import com.PickOne.domain.preference.dto.response.PreferenceDetailDto;
import com.PickOne.domain.preference.model.QUserGenre;
import com.PickOne.domain.preference.model.QUserInstrument;
import com.PickOne.domain.preference.model.entity.QPreference;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PreferenceRepositoryCustomImpl implements PreferenceRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    public PreferenceDetailDto findPreferenceDetailById(Long id) {
        QPreference preference = QPreference.preference;
        QUserInstrument userInstrument = QUserInstrument.userInstrument;
        QUserGenre userGenre = QUserGenre.userGenre;

        List<PreferenceDetailDto> results = queryFactory.select(Projections.constructor(PreferenceDetailDto.class,
                        preference.mbti.stringValue(),
                        preference.selfDescription,
                        preference.region,
                        preference.university,
                        preference.major,
                        Projections.list(
                                Projections.constructor(GenreDto.class,
                                        userGenre.genre.stringValue())
                        ),
                        // 악기 목록
                        Projections.list(
                                Projections.constructor(InstrumentDto.class,
                                        userInstrument.instrument.stringValue(),
                                        userInstrument.proficiency.stringValue(),
                                        userInstrument.startedPlaying)
                        )
                ))
                .distinct() // 중복 제거
                .from(preference)
                // Join userGenre
                .leftJoin(userGenre).on(userGenre.preference.eq(preference))
                // Join userInstrument
                .leftJoin(userInstrument).on(userInstrument.preference.eq(preference))
                .where(preference.preferenceId.eq(id))
                .fetch();

        return results.isEmpty() ? null : results.get(0);
    }

    public List<PreferenceDetailDto> findAllPreferenceDetail() {
        QPreference preference = QPreference.preference;
        QUserInstrument userInstrument = QUserInstrument.userInstrument;
        QUserGenre userGenre = QUserGenre.userGenre;

        return queryFactory.select(Projections.constructor(PreferenceDetailDto.class,
                        preference.mbti.stringValue(),
                        preference.selfDescription,
                        preference.region,
                        preference.university,
                        preference.major,
                        // 장르 목록
                        Projections.list(
                                Projections.constructor(GenreDto.class,
                                        userGenre.genre.stringValue())
                        ),
                        // 악기 목록
                        Projections.list(
                                Projections.constructor(InstrumentDto.class,
                                        userInstrument.instrument.stringValue(),
                                        userInstrument.proficiency.stringValue(),
                                        userInstrument.startedPlaying)
                        )
                ))
                .distinct() // 중복 제거
                .from(preference)
                .leftJoin(userGenre).on(userGenre.preference.eq(preference)) // 장르와 조인
                .leftJoin(userInstrument).on(userInstrument.preference.eq(preference)) // 악기와 조인
                .fetch();
    }

}
