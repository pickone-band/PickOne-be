package com.PickOne.domain.preference.controller;

import com.PickOne.domain.preference.dto.request.PreferenceRegisterDto;
import com.PickOne.domain.preference.dto.request.UserGenreRequestDto;
import com.PickOne.domain.preference.dto.request.UserInstrumentRequestDto;
import com.PickOne.domain.preference.dto.response.PreferenceDetailDto;
import com.PickOne.domain.preference.service.PreferenceService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/preference")
@RequiredArgsConstructor

public class PreferenceController {
    private final PreferenceService preferenceService;

    @GetMapping("/detail")
    public ResponseEntity<BaseResponse<PreferenceDetailDto>> getPreferenceInfo(
            @RequestParam Long preferenceId) {
        return BaseResponse.success(preferenceService.getPreferenceDetail(preferenceId));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<PreferenceDetailDto>>> getAllPreference() {
        return BaseResponse.success(preferenceService.getAllPreferenceDetail());
    }


    @PostMapping()
    public ResponseEntity<BaseResponse<Void>> savePreferenceInfo(
            @RequestBody @Valid PreferenceRegisterDto preferenceRegisterDto,
            @RequestParam Long memberId) { //시큐리티 적용 후 @AuthenticationPrincipal 바꿀 예정

        preferenceService.registerPreference(preferenceRegisterDto, memberId);

        if (preferenceRegisterDto.getUserGenres() != null) {

            UserGenreRequestDto userGenreRequestDto = UserGenreRequestDto.builder()
                    .genre(preferenceRegisterDto.getUserGenres())
                    .build();
            preferenceService.registerUserGenre(userGenreRequestDto, memberId);
        }
        if (preferenceRegisterDto.getUserInstrumentDetails() != null) {

            UserInstrumentRequestDto userInstrumentRequestDto = UserInstrumentRequestDto.builder()
                    .instrumentDetails(preferenceRegisterDto.getUserInstrumentDetails())
                    .build();
            preferenceService.registerUserInstrument(userInstrumentRequestDto, memberId);
        }

        return BaseResponse.success(SuccessCode.CREATED);
    }
}
