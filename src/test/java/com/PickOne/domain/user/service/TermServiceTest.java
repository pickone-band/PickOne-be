package com.PickOne.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.PickOne.domain.user.dto.TermDto.*;
import com.PickOne.domain.user.model.Term;
import com.PickOne.domain.user.repository.TermRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @Test
    void 모든_약관을_가져온다() {
        // Given
        Term term1 = Term.builder()
                .id(1L)
                .title("약관 제목 1")
                .version("1.0")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .build();

        Term term2 = Term.builder()
                .id(2L)
                .title("약관 제목 2")
                .version("1.1")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(20))
                .build();

        when(termRepository.findAll()).thenReturn(List.of(term1, term2));

        // When
        List<TermResponseDto> result = termService.getAllTerms();

        // Then
        assertEquals(2, result.size());
        assertEquals("약관 제목 1", result.get(0).getTitle());
        assertEquals("약관 제목 2", result.get(1).getTitle());
        verify(termRepository, times(1)).findAll();
    }

    @Test
    void 특정_약관을_ID로_가져온다() {
        // Given
        Term term = Term.builder()
                .id(1L)
                .title("약관 제목")
                .version("1.0")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .build();

        when(termRepository.findById(1L)).thenReturn(Optional.of(term));

        // When
        TermResponseDto result = termService.getTerm(1L);

        // Then
        assertNotNull(result);
        assertEquals("약관 제목", result.getTitle());
        assertEquals("1.0", result.getVersion());
        verify(termRepository, times(1)).findById(1L);
    }

    @Test
    void 특정_약관을_찾지_못하면_예외를_던진다() {
        // Given
        when(termRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> termService.getTerm(1L));
        assertEquals(ErrorCode.TERM_NOT_FOUND, exception.getErrorCode());
        verify(termRepository, times(1)).findById(1L);
    }
}
