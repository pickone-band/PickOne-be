package com.PickOne.domain.user.service;


import com.PickOne.domain.user.dto.SocialAccountDto.*;
import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.SocialAccount;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.domain.user.repository.SocialAccountRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SocialAccountService {

    private final SocialAccountRepository socialAccountRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public SocialAccountResponseDto createSocialAccount(SocialAccountCreateDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        if (socialAccountRepository.existsByMemberIdAndProvider(member.getId(), dto.getProvider())) {
            throw new BusinessException(ErrorCode.DUPLICATE_SOCIAL_ACCOUNT);
        }

        SocialAccount account = modelMapper.map(dto, SocialAccount.class);
        account.setMember(member);
        SocialAccount saved = socialAccountRepository.save(account);
        return modelMapper.map(saved, SocialAccountResponseDto.class);
    }

    @Transactional(readOnly = true)
    public SocialAccountResponseDto getSocialAccount(Long id) {
        SocialAccount account = socialAccountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND));
        return modelMapper.map(account, SocialAccountResponseDto.class);
    }

    @Transactional(readOnly = true)
    public List<SocialAccountResponseDto> getAllSocialAccounts() {
        return socialAccountRepository.findAll().stream()
                .map(a -> modelMapper.map(a, SocialAccountResponseDto.class))
                .collect(Collectors.toList());
    }

    public SocialAccountResponseDto updateSocialAccount(Long id, SocialAccountUpdateDto dto) {
        SocialAccount account = socialAccountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND));

        // provider 변경 시, 중복 검사
        if (dto.getProvider() != null
                && !dto.getProvider().equals(account.getProvider())
                && socialAccountRepository.existsByMemberIdAndProvider(account.getMember().getId(), dto.getProvider())) {
            throw new BusinessException(ErrorCode.DUPLICATE_SOCIAL_ACCOUNT);
        }
        account.setProvider(dto.getProvider());
        account.setProviderUserId(dto.getProviderUserId());
        return modelMapper.map(account, SocialAccountResponseDto.class);
    }

    public void deleteSocialAccount(Long id) {
        SocialAccount account = socialAccountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND));
        socialAccountRepository.delete(account);
    }
}
