package com.PickOne.domain.preference.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.PickOne.domain.preference.dto.request.PreferenceRegisterDto;
import com.PickOne.domain.preference.dto.request.UserGenreRequestDto;
import com.PickOne.domain.preference.dto.request.UserInstrumentRequestDto;
import com.PickOne.domain.preference.model.UserGenre;
import com.PickOne.domain.preference.model.UserInstrument;
import com.PickOne.domain.preference.model.entity.Genre;
import com.PickOne.domain.preference.model.entity.Instrument;
import com.PickOne.domain.preference.model.entity.Mbti;
import com.PickOne.domain.preference.model.entity.Preference;
import com.PickOne.domain.preference.model.entity.Proficiency;
import com.PickOne.domain.preference.repository.PreferenceRepository;
import com.PickOne.domain.preference.repository.UserGenreRepository;
import com.PickOne.domain.preference.repository.UserInstrumentRepository;
import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PreferenceServiceTest {

    @Autowired
    private PreferenceService preferenceService;

    @Autowired
    private PreferenceRepository preferenceRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private UserInstrumentRepository userInstrumentRepository;
    @Autowired
    private UserGenreRepository userGenreRepository;

    @Test
    void 취향등록테스트() {
        preferenceRepository.deleteAll();
        memberRepository.deleteAll();
        userInstrumentRepository.deleteAll();

        PreferenceRegisterDto preferenceRegisterDto = PreferenceRegisterDto.builder()
                .mbti(Mbti.ENFP)
                .selfDescription("안녕하세요 반가워요")
                .region("서울 성동구")
                .university("서울예술대학교")
                .major("도자기공예학과")
                .build();

        UserInstrumentRequestDto userInstrumentRequestDto = UserInstrumentRequestDto.builder()
                .instrumentDetails(List.of(
                        UserInstrumentRequestDto.InstrumentDetail.builder()
                                .instrument(Instrument.BASS)
                                .proficiency(Proficiency.ADVANCED)
                                .startedPlaying(LocalDate.of(2012, 3, 1))
                                .build()
                ))
                .build();

        UserGenreRequestDto userGenreRequestDto = UserGenreRequestDto.builder()
                .genre(List.of(Genre.INDIE_ROCK, Genre.POST_ROCK))
                .build();

        Member member = memberRepository.save(
                Member.builder()
                        .loginId("thunder123")
                        .password("1q2w3e4r")
                        .username("김영한")
                        .email("test@example.com")
                        .nickname("시골코딩천재")
                        .build()
        );

        preferenceService.registerPreference(preferenceRegisterDto, member.getId());
        preferenceService.registerUserInstrument(userInstrumentRequestDto, member.getId());
        preferenceService.registerUserGenre(userGenreRequestDto, member.getId());

        Preference savedPreference = preferenceRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PREFERENCE_NOT_FOUND));

        assertThat(savedPreference.getMbti()).isEqualTo(preferenceRegisterDto.getMbti());
        assertThat(savedPreference.getRegion()).isEqualTo(preferenceRegisterDto.getRegion());

        //유저악기테스트
        List<UserInstrument> savedInstruments = userInstrumentRepository.findAll();
        assertThat(savedInstruments).hasSize(1);
        UserInstrument savedInstrument = savedInstruments.get(0);
        assertThat(savedInstrument.getInstrument()).isEqualTo(Instrument.BASS);

        //유저장르 테스트
        List<UserGenre> savedGenres = userGenreRepository.findAll();
        assertThat(savedGenres).hasSize(2);

        UserGenre savedGenre1 = savedGenres.get(0);
        assertThat(savedGenre1.getGenre()).isEqualTo(Genre.INDIE_ROCK);

        UserGenre savedGenre2 = savedGenres.get(1);
        assertThat(savedGenre2.getGenre()).isNotEqualTo(Genre.SHOEGAZING);
    }
}