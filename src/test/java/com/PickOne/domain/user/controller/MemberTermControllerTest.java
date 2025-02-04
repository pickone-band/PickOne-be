package com.PickOne.domain.user.controller;

import com.PickOne.PickOneApplication;
import com.PickOne.domain.user.dto.MemberTermDto.MemberTermResponseDto;
import com.PickOne.domain.user.service.MemberTermService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MemberTermAdminController 단위 테스트
 * @WebMvcTest: 컨트롤러 관련 빈만 로딩
 */
@WebMvcTest(
        controllers = MemberTermController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = PickOneApplication.class
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class MemberTermControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberTermService memberTermService;

    @Test
    @DisplayName("[GET] /api/admin/member-terms - 전체 조회 성공")
    void getAllMemberTerms_Success() throws Exception {
        // Given
        MemberTermResponseDto dto1 = MemberTermResponseDto.builder()
                .id(1L)
                .memberId(10L)
                .termId(100L)
                .isAgreed(true)
                .build();
        MemberTermResponseDto dto2 = MemberTermResponseDto.builder()
                .id(2L)
                .memberId(11L)
                .termId(101L)
                .isAgreed(false)
                .build();

        given(memberTermService.getAllMemberTerms()).willReturn(asList(dto1, dto2));

        // When & Then
        mockMvc.perform(get("/api/admin/member-terms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result[0].id").value(1))
                .andExpect(jsonPath("$.result[0].memberId").value(10))
                .andExpect(jsonPath("$.result[0].termId").value(100))
                .andExpect(jsonPath("$.result[0].isAgreed").value(true))
                .andExpect(jsonPath("$.result[1].id").value(2))
                .andExpect(jsonPath("$.result[1].memberId").value(11))
                .andExpect(jsonPath("$.result[1].termId").value(101))
                .andExpect(jsonPath("$.result[1].isAgreed").value(false));
    }

    @Test
    @DisplayName("[GET] /api/admin/member-terms/{memberTermId} - 단건 조회 성공")
    void getMemberTerm_Success() throws Exception {
        // Given
        MemberTermResponseDto dto = MemberTermResponseDto.builder()
                .id(100L)
                .memberId(20L)
                .termId(200L)
                .isAgreed(true)
                .build();

        given(memberTermService.getMemberTerm(100L)).willReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/admin/member-terms/{memberTermId}", 100)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.id").value(100))
                .andExpect(jsonPath("$.result.memberId").value(20))
                .andExpect(jsonPath("$.result.termId").value(200))
                .andExpect(jsonPath("$.result.isAgreed").value(true));
    }

    @Test
    @DisplayName("[GET] /api/admin/member-terms/by-member/{memberId} - 특정 회원 동의 내역 조회 성공")
    void getMemberTermsByMember_Success() throws Exception {
        // Given
        MemberTermResponseDto dto1 = MemberTermResponseDto.builder()
                .id(1L)
                .memberId(50L)
                .termId(10L)
                .isAgreed(true)
                .build();
        MemberTermResponseDto dto2 = MemberTermResponseDto.builder()
                .id(2L)
                .memberId(50L)
                .termId(11L)
                .isAgreed(false)
                .build();

        given(memberTermService.getMemberTermsByMember(50L)).willReturn(asList(dto1, dto2));

        // When & Then
        mockMvc.perform(get("/api/admin/member-terms/by-member/{memberId}", 50)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result[0].id").value(1))
                .andExpect(jsonPath("$.result[0].memberId").value(50))
                .andExpect(jsonPath("$.result[0].termId").value(10))
                .andExpect(jsonPath("$.result[0].isAgreed").value(true))
                .andExpect(jsonPath("$.result[1].id").value(2))
                .andExpect(jsonPath("$.result[1].memberId").value(50))
                .andExpect(jsonPath("$.result[1].termId").value(11))
                .andExpect(jsonPath("$.result[1].isAgreed").value(false));
    }

    @Test
    @DisplayName("[GET] /api/admin/member-terms/by-term/{termId} - 특정 약관에 동의한 회원 목록 조회 성공")
    void getMemberTermsByTerm_Success() throws Exception {
        // Given
        MemberTermResponseDto dto1 = MemberTermResponseDto.builder()
                .id(1L)
                .memberId(70L)
                .termId(999L)
                .isAgreed(true)
                .build();
        MemberTermResponseDto dto2 = MemberTermResponseDto.builder()
                .id(2L)
                .memberId(71L)
                .termId(999L)
                .isAgreed(true)
                .build();

        given(memberTermService.getMemberTermsByTerm(999L)).willReturn(asList(dto1, dto2));

        // When & Then
        mockMvc.perform(get("/api/admin/member-terms/by-term/{termId}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result[0].id").value(1))
                .andExpect(jsonPath("$.result[0].memberId").value(70))
                .andExpect(jsonPath("$.result[0].termId").value(999))
                .andExpect(jsonPath("$.result[0].isAgreed").value(true))
                .andExpect(jsonPath("$.result[1].id").value(2))
                .andExpect(jsonPath("$.result[1].memberId").value(71))
                .andExpect(jsonPath("$.result[1].termId").value(999))
                .andExpect(jsonPath("$.result[1].isAgreed").value(true))
                .andExpect(jsonPath("$.result.length()").value(2));
    }

    @Test
    @DisplayName("[GET] /api/admin/member-terms/{memberTermId} - 단건 조회 실패 (약관 없음)")
    void getMemberTerm_Failure_TermNotFound() throws Exception {
        // Given
        willThrow(new BusinessException(ErrorCode.MEMBER_TERM_NOT_FOUND))
                .given(memberTermService).getMemberTerm(1L);

        // When & Then
        mockMvc.perform(get("/api/admin/member-terms/{memberTermId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.MEMBER_TERM_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.result").doesNotExist());
    }
}
