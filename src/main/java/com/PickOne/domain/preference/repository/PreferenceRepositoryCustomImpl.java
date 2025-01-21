package com.PickOne.domain.preference.repository;

import com.PickOne.domain.preference.dto.response.GenreDto;
import com.PickOne.domain.preference.dto.response.InstrumentDto;
import com.PickOne.domain.preference.dto.response.PreferenceDetailDto;
import com.PickOne.domain.preference.model.QUserGenre;
import com.PickOne.domain.preference.model.QUserInstrument;
import com.PickOne.domain.preference.model.entity.QPreference;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

        List<Tuple> results = queryFactory
                .select(preference.preferenceId,
                        preference.mbti.stringValue(),
                        preference.selfDescription,
                        preference.region,
                        preference.university,
                        preference.major,
                        userGenre.genre.stringValue(),
                        userInstrument.instrument.stringValue(),
                        userInstrument.proficiency.stringValue(),
                        userInstrument.startedPlaying)
                .from(preference)
                .leftJoin(userGenre).on(userGenre.preference.eq(preference))
                .leftJoin(userInstrument).on(userInstrument.preference.eq(preference))
                .fetch();

        // Map을 사용해 회원별 데이터 그룹화
        Map<Long, PreferenceDetailDto> preferenceMap = new LinkedHashMap<>();
        for (Tuple tuple : results) {
            Long id = tuple.get(preference.preferenceId);

            // 기존 PreferenceDetailDto 가져오기 또는 새로 생성
            PreferenceDetailDto detailDto = preferenceMap.computeIfAbsent(id, key -> new PreferenceDetailDto(
                    tuple.get(preference.mbti.stringValue()),
                    tuple.get(preference.selfDescription),
                    tuple.get(preference.region),
                    tuple.get(preference.university),
                    tuple.get(preference.major),
                    new ArrayList<>(),  // userGenres
                    new ArrayList<>()   // userInstruments
            ));

            // 장르 추가
            String genreName = tuple.get(userGenre.genre.stringValue());
            if (genreName != null) {
                detailDto.getUserGenres().add(new GenreDto(genreName));
            }

            // 악기 추가
            String instrument = tuple.get(userInstrument.instrument.stringValue());
            String proficiency = tuple.get(userInstrument.proficiency.stringValue());
            LocalDate startedPlaying = tuple.get(userInstrument.startedPlaying);
            if (instrument != null) {
                detailDto.getUserInstruments().add(new InstrumentDto(instrument, proficiency, startedPlaying));
            }
        }

        // Map에서 값만 반환
        return new ArrayList<>(preferenceMap.values());
    }

}
