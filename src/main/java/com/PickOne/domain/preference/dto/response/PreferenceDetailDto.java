package com.PickOne.domain.preference.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreferenceDetailDto {
    private String mbti;   // 사용자 MBTI

    private String selfDescription; //자기 소개

    private String region; //선호 활동 지역

    private String university; // 대학교 이름

    private String major;      // 전공

    private List<GenreDto> userGenres; // 선호 장르 리스트
    private List<InstrumentDto> userInstruments; // 악기 상세 정보 리스트

    public PreferenceDetailDto(
            String mbti,
            String selfDescription,
            String region,
            String university,
            String major,
            List<GenreDto> userGenres,
            List<InstrumentDto> userInstruments
    ) {
        this.mbti = mbti;
        this.selfDescription = selfDescription;
        this.region = region;
        this.university = university;
        this.major = major;
        this.userGenres = userGenres;
        this.userInstruments = userInstruments;
    }
}


