package com.PickOne.domain.preference.repository;

import com.PickOne.domain.preference.dto.response.PreferenceDetailDto;
import java.util.List;

public interface PreferenceRepositoryCustom {
    PreferenceDetailDto findPreferenceDetailById(Long Id);

    List<PreferenceDetailDto> findAllPreferenceDetail();
}
