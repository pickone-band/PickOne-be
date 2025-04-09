package com.PickOne.domain.preference.service;

import com.PickOne.domain.preference.dto.request.PreferenceRegisterDto;
import com.PickOne.domain.preference.dto.request.UserGenreRequestDto;
import com.PickOne.domain.preference.dto.request.UserInstrumentRequestDto;
import com.PickOne.domain.preference.dto.response.PreferenceDetailDto;
import com.PickOne.domain.preference.dto.response.PreferenceResponseDto;
import com.PickOne.domain.preference.model.UserGenre;
import com.PickOne.domain.preference.model.UserInstrument;
import com.PickOne.domain.preference.model.entity.Preference;
import com.PickOne.domain.preference.repository.PreferenceRepository;
import com.PickOne.domain.preference.repository.UserGenreRepository;
import com.PickOne.domain.preference.repository.UserInstrumentRepository;
import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreferenceService {
    private final PreferenceRepository preferenceRepository;
    private final MemberRepository memberRepository;
    private final UserInstrumentRepository userInstrumentRepository;
    private final UserGenreRepository userGenreRepository;


    @Transactional
    public PreferenceResponseDto loadPreference(Long memberId) {
        Member member = memberRepository.findById(memberId)     //시큐리티 적용후 memberId로 변경예정
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        Preference preference = preferenceRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PREFERENCE_NOT_FOUND));

        return PreferenceResponseDto.fromEntity(preference);
    }

    @Transactional
    public PreferenceDetailDto getPreferenceDetail(Long preferenceId) {
        PreferenceDetailDto preferenceDetailDto = preferenceRepository.findPreferenceDetailById(preferenceId);
        return preferenceDetailDto;
    }

    @Transactional
    public List<PreferenceDetailDto> getAllPreferenceDetail() {
        List<PreferenceDetailDto> preferenceDetailDtoList = preferenceRepository.findAllPreferenceDetail();
        return preferenceDetailDtoList;
    }


    @Transactional
    public void registerPreference(
            PreferenceRegisterDto preferenceRegisterDto
            , Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

        preferenceRepository.findByMemberId(memberId)
                .ifPresent(preference -> {
                    throw new IllegalStateException("이미 존재합니다.");
                });
        Preference preference = preferenceRegisterDto.toEntity(member);

        preferenceRepository.save(preference);
    }

    @Transactional
    public void registerUserInstrument(
            UserInstrumentRequestDto userInstrumentRequestDto
            , Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        Preference preference = preferenceRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PREFERENCE_NOT_FOUND));

        List<UserInstrument> userInstruments = userInstrumentRequestDto.toEntityList(member, preference);
        userInstrumentRepository.saveAll(userInstruments);
    }

    @Transactional
    public void registerUserGenre(
            UserGenreRequestDto userGenreRequestDto
            , Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        Preference preference = preferenceRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PREFERENCE_NOT_FOUND));

        List<UserGenre> userGenres = userGenreRequestDto.toEntityList(member, preference);

        userGenreRepository.saveAll(userGenres);
    }
}
