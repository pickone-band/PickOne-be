package com.PickOne.domain.user.controller;

import com.PickOne.domain.user.dto.MemberStateDto.*;
import com.PickOne.domain.user.service.MemberStateService;
import com.PickOne.global.config.SecurityConfig;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.PickOneApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MemberStateController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = PickOneApplication.class
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class MemberStateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberStateService memberStateService;

    @Test
    @DisplayName("회원 상태 단건 조회 API 테스트")
    void getMemberState() throws Exception {
        // given
        MemberStateResponseDto responseDto = new MemberStateResponseDto();
        responseDto.setId(1L);
        responseDto.setStatus("ACTIVE");

        when(memberStateService.getMemberState(1L)).thenReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/member-states/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("회원 상태 전체 조회 API 테스트")
    void getAllMemberStates() throws Exception {
        // given
        MemberStateResponseDto responseDto1 = new MemberStateResponseDto();
        responseDto1.setId(1L);
        responseDto1.setStatus("ACTIVE");

        MemberStateResponseDto responseDto2 = new MemberStateResponseDto();
        responseDto2.setId(2L);
        responseDto2.setStatus("BANNED");

        List<MemberStateResponseDto> responseDtoList = Arrays.asList(responseDto1, responseDto2);
        when(memberStateService.getAllStates()).thenReturn(responseDtoList);

        // when & then
        mockMvc.perform(get("/api/member-states"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.result[1].status").value("BANNED"));
    }
}
