package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.TermDto.*;
import com.PickOne.domain.user.model.Term;
import com.PickOne.domain.user.repository.TermRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TermService {

    private final TermRepository termRepository;
    private final ModelMapper modelMapper;

    public void createTerm(TermCreateDto dto) {
        // 제목과 버전 중복 체크
        if (termRepository.existsByTitleAndVersion(dto.getTitle(), dto.getVersion())) {
            throw new BusinessException(ErrorCode.DUPLICATE_TERM_VERSION);
        }

        Term term = Term.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .version(dto.getVersion())
                .isRequired(dto.getIsRequired())
                .isActive(dto.getIsActive())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        termRepository.save(term);
    }

}
