package com.PickOne.domain.preference.dto.request;

import com.PickOne.domain.preference.model.entity.Genre;
import com.PickOne.domain.preference.model.entity.Mbti;
import com.PickOne.domain.preference.model.entity.Preference;
import com.PickOne.domain.user.model.Member;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PreferenceRegisterDto {

    private List<Genre> userGenres; // 선호 장르 리스트
    private List<UserInstrumentRequestDto.InstrumentDetail> userInstrumentDetails; // 악기 상세 정보 리스트

    private Mbti mbti;   // 사용자 MBTI
    private String selfDescription; //자기 소개
    private String region; //선호 활동 지역
    private String university; // 대학교 이름
    private String major;      // 전공

    public Preference toEntity(Member member) {
        return Preference.builder()
                .member(member)
                .mbti(this.mbti)
                .selfDescription(this.selfDescription)
                .region(this.region)
                .university(this.university)
                .major(this.major)
                .build();
    }
}
