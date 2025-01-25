package com.PickOne.domain.user.controller;

import com.PickOne.PickOneApplication;
import com.PickOne.domain.user.dto.TermDto.*;
import com.PickOne.domain.user.service.TermService;
import com.PickOne.domain.user.controller.TermController;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.global.exception.SuccessCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = TermController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = PickOneApplication.class
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class TermControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TermService termService;

    @Test
    @DisplayName("약관 생성 테스트 - 성공")
    void 약관_생성_테스트_성공() throws Exception {
        // given
        willDoNothing().given(termService).createTerm(any(TermCreateDto.class));

        // when & then
        mockMvc.perform(post("/api/terms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "약관 제목",
                                  "content": "약관 내용",
                                  "version": "1.0",
                                  "isRequired": true,
                                  "isActive": true,
                                  "startDate": "2023-01-01",
                                  "endDate": "2023-12-31"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value(SuccessCode.CREATED.getMessage()))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @Test
    @DisplayName("모든 약관 조회 - 성공")
    void 모든_약관_조회_성공() throws Exception {
        // Given
        TermResponseDto dto1 = new TermResponseDto();
        dto1.setTitle("약관 제목 1");
        dto1.setVersion("1.0");

        TermResponseDto dto2 = new TermResponseDto();
        dto2.setTitle("약관 제목 2");
        dto2.setVersion("1.1");

        given(termService.getAllTerms()).willReturn(List.of(dto1, dto2));

        // When & Then
        mockMvc.perform(get("/api/terms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value(SuccessCode.OK.getMessage()))
                .andExpect(jsonPath("$.result[0].title").value("약관 제목 1"))
                .andExpect(jsonPath("$.result[1].title").value("약관 제목 2"));
    }

    @Test
    @DisplayName("특정 약관 조회 - 성공")
    void 특정_약관_조회_성공() throws Exception {
        // Given
        TermResponseDto dto = new TermResponseDto();
        dto.setTitle("약관 제목");
        dto.setVersion("1.0");

        given(termService.getTerm(1L)).willReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/terms/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value(SuccessCode.OK.getMessage()))
                .andExpect(jsonPath("$.result.title").value("약관 제목"))
                .andExpect(jsonPath("$.result.version").value("1.0"));
    }

    @Test
    @DisplayName("특정 약관 조회 - 약관 없음")
    void 특정_약관_조회_실패() throws Exception {
        // Given
        willThrow(new BusinessException(ErrorCode.TERM_NOT_FOUND)).given(termService).getTerm(1L);

        // When & Then
        mockMvc.perform(get("/api/terms/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.TERM_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

}