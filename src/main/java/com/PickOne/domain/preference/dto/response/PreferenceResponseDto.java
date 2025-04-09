package com.PickOne.domain.preference.dto.response;

import com.PickOne.domain.preference.model.entity.Preference;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PreferenceResponseDto {
    private String mbti;   // 사용자 MBTI

    private String selfDescription; //자기 소개

    private String region; //선호 활동 지역

    private String university; // 대학교 이름

    private String major;      // 전공

    public static PreferenceResponseDto fromEntity(Preference preference) {
        return PreferenceResponseDto.builder()
                .mbti(preference.getMbti().name()) // Enum 값은 name()으로 변환
                .selfDescription(preference.getSelfDescription())
                .region(preference.getRegion())
                .university(preference.getUniversity())
                .major(preference.getMajor())
                .build();
    }
}
