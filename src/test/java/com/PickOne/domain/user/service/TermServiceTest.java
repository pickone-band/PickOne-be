package com.PickOne.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.PickOne.domain.user.dto.TermDto.*;
import com.PickOne.domain.user.model.Term;
import com.PickOne.domain.user.repository.TermRepository;
import com.PickOne.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

class TermServiceTest {

    @Mock
    private TermRepository termRepository;

    private ModelMapper modelMapper;

    @InjectMocks
    private TermService termService;

    @BeforeEach
    void 설정() {
        MockitoAnnotations.openMocks(this);
        modelMapper = new ModelMapper(); // 실제 ModelMapper 사용
        termService = new TermService(termRepository, modelMapper); // Service에 ModelMapper 주입
    }

    @Test
    void 제목과_버전이_중복되면_예외를_던진다() {
        // Given
        TermCreateDto dto = new TermCreateDto();
        dto.setTitle("테스트 제목");
        dto.setVersion("1.0");
        dto.setContent("테스트 내용");
        dto.setIsRequired(true);
        dto.setIsActive(true);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(10));

        when(termRepository.existsByTitleAndVersion(dto.getTitle(), dto.getVersion())).thenReturn(true);

        // When & Then
        assertThrows(BusinessException.class, () -> termService.createTerm(dto));
        verify(termRepository, times(1)).existsByTitleAndVersion(dto.getTitle(), dto.getVersion());
        verify(termRepository, never()).save(any(Term.class));
    }

    @Test
    void 제목과_버전이_중복되지_않으면_저장한다() {
        // Given
        TermCreateDto dto = new TermCreateDto();
        dto.setTitle("테스트 제목");
        dto.setVersion("1.0");
        dto.setContent("테스트 내용");
        dto.setIsRequired(true);
        dto.setIsActive(true);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(10));

        when(termRepository.existsByTitleAndVersion(dto.getTitle(), dto.getVersion())).thenReturn(false);

        // When
        termService.createTerm(dto);

        // Then
        verify(termRepository, times(1)).existsByTitleAndVersion(dto.getTitle(), dto.getVersion());
        verify(termRepository, times(1)).save(any(Term.class));
    }
}
