package com.PickOne.domain.preference.dto.response;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstrumentDto {
    private String instrument;
    private String proficiency;
    private LocalDate startedPlaying;

    public InstrumentDto(String instrument, String proficiency, LocalDate startedPlaying) {
        this.instrument = instrument;
        this.proficiency = proficiency;
        this.startedPlaying = startedPlaying;
    }
}
