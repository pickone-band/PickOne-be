package com.PickOne.domain.preference.dto.request;

import com.PickOne.domain.preference.model.UserInstrument;
import com.PickOne.domain.preference.model.entity.Instrument;
import com.PickOne.domain.preference.model.entity.Preference;
import com.PickOne.domain.preference.model.entity.Proficiency;
import com.PickOne.domain.user.model.entity.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInstrumentRequestDto {

    private List<InstrumentDetail> instrumentDetails; // 악기, 숙련도, 시작일을 포함하는 리스트

    @Getter
    @Builder
    public static class InstrumentDetail {
        private Instrument instrument;       // 악기
        private Proficiency proficiency;     // 숙련도
        private LocalDate startedPlaying;    // 시작일
    }

    public List<UserInstrument> toEntityList(Member member, Preference preference) {
        return this.instrumentDetails.stream()
                .map(detail -> {
                    UserInstrument userInstrument = UserInstrument.builder()
                            .member(member)
                            .preference(preference)
                            .instrument(detail.getInstrument())
                            .proficiency(detail.getProficiency())
                            .startedPlaying(detail.getStartedPlaying())
                            .build();
                    return userInstrument;
                })
                .collect(Collectors.toList());
    }
}
