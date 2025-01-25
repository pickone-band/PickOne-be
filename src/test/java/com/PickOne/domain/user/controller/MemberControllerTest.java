package com.PickOne.domain.user.controller;

import com.PickOne.PickOneApplication;
import com.PickOne.domain.user.dto.MemberDto.*;
import com.PickOne.domain.user.service.MemberService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @WebMvcTest
 *  - Controller 계층만 로딩하여 테스트하는 슬라이스 테스트.
 *  - Service, Repository 등 다른 Bean들은 기본적으로 로드되지 않음.
 */
@WebMvcTest(
        controllers = MemberController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = PickOneApplication.class // ★ 메인 클래스 제외 ★
                )
        },
        excludeAutoConfiguration = {
                // JPA 관련 자동설정 빼기
                org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
                org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false) // 시큐리티 등 필터 꺼두기(필요 시)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Controller가 주입받는 Service를 MockBean으로 등록
     */
    @MockBean
    private MemberService memberService;


    @Test
    @DisplayName("회원 가입 테스트 - 성공")
    void signUpTest() throws Exception {
        // given
        // "회원 생성" 로직은 아무 일도 일어나지 않는다고 가정
        willDoNothing().given(memberService).createMember(any(MemberCreateDto.class));

        // when & then
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loginId": "testUser",
                                  "password": "testPass",
                                  "username": "유저",
                                  "email": "user@test.com",
                                  "nickname": "닉네임"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value(SuccessCode.CREATED.getMessage()));
    }


    @Test
    @DisplayName("전체 회원 조회 테스트")
    void getAllTest() throws Exception {
        // given
        MemberResponseDto dto1 = new MemberResponseDto();
        dto1.setId(1L);
        dto1.setLoginId("user1");
        dto1.setUsername("유저1");
        dto1.setEmail("user1@test.com");
        dto1.setNickname("닉네임1");

        MemberResponseDto dto2 = new MemberResponseDto();
        dto2.setId(2L);
        dto2.setLoginId("user2");
        dto2.setUsername("유저2");
        dto2.setEmail("user2@test.com");
        dto2.setNickname("닉네임2");

        List<MemberResponseDto> responseList = Arrays.asList(dto1, dto2);

        // Service에서 getAllMembers() 호출 시, 위 responseList를 반환한다고 Mock
        given(memberService.getAllMembers()).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/members")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value(SuccessCode.OK.getMessage()))
                .andExpect(jsonPath("$.result[0].username").value("유저1"))
                .andExpect(jsonPath("$.result[1].username").value("유저2"));
    }


    @Test
    @DisplayName("단일 회원 조회 테스트 - 성공")
    void getOneTest() throws Exception {
        // given
        MemberResponseDto dto = new MemberResponseDto();
        dto.setId(1L);
        dto.setLoginId("user1");
        dto.setUsername("유저1");
        dto.setEmail("user1@test.com");
        dto.setNickname("닉네임1");

        // Service 호출 시 dto 반환
        given(memberService.getMember(1L)).willReturn(dto);

        // when & then
        mockMvc.perform(get("/api/members/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value(SuccessCode.OK.getMessage()))
                .andExpect(jsonPath("$.result.username").value("유저1"))
                .andExpect(jsonPath("$.result.email").value("user1@test.com"));
    }

    @Test
    @DisplayName("회원 삭제 - 성공")
    void deleteMemberTest_success() throws Exception {
        // given
        Long memberId = 1L;
        doNothing().when(memberService).deleteMember(memberId);

        // when & then
        mockMvc.perform(delete("/api/members/{id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value(SuccessCode.DELETED.getMessage()));
    }

    @Test
    @DisplayName("회원 삭제 - 실패 (존재하지 않는 회원)")
    void deleteMemberTest_notFound() throws Exception {
        // given
        Long invalidId = 999L;
        doThrow(new BusinessException(ErrorCode.USER_INFO_NOT_FOUND))
                .when(memberService).deleteMember(invalidId);

        // when & then
        mockMvc.perform(delete("/api/members/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_INFO_NOT_FOUND.getMessage()));
    }



    @Test
    @DisplayName("회원 정보 업데이트 성공 테스트")
    void updateMember_Success() throws Exception {
        // given
        // 업데이트가 성공적으로 진행되었다고 가정
        MemberResponseDto updatedDto = new MemberResponseDto();
        updatedDto.setId(1L);
        updatedDto.setUsername("업데이트유저");
        updatedDto.setNickname("업데이트닉네임");

        // Service가 성공적으로 업데이트된 결과를 리턴한다고 Mock
        given(memberService.updateMember(eq(1L), any(MemberUpdateDto.class)))
                .willReturn(updatedDto);

        // when & then
        mockMvc.perform(patch("/api/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "업데이트유저",
                                  "nickname": "업데이트닉네임"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value(SuccessCode.UPDATED.getMessage()));
    }


    @Test
    @DisplayName("회원 정보 업데이트 실패 - 닉네임 중복")
    void updateMember_DuplicateNickname() throws Exception {
        // given
        // 닉네임 중복 시 발생하는 예외를 던지도록 Mock
        willThrow(new BusinessException(ErrorCode.DUPLICATE_NICKNAME))
                .given(memberService).updateMember(eq(1L), any(MemberUpdateDto.class));

        // when & then
        mockMvc.perform(patch("/api/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "업데이트유저",
                                  "nickname": "중복닉네임"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_NICKNAME.getMessage()));
    }


    @Test
    @DisplayName("회원 정보 업데이트 실패 - 회원 없음(404)")
    void updateMember_NotFound() throws Exception {
        // given
        // 존재하지 않는 회원 ID 요청 시 발생하는 예외를 던지도록 Mock
        willThrow(new BusinessException(ErrorCode.USER_INFO_NOT_FOUND))
                .given(memberService).updateMember(eq(999L), any(MemberUpdateDto.class));

        // when & then
        mockMvc.perform(patch("/api/members/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "업데이트유저",
                                  "nickname": "업데이트닉네임"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_INFO_NOT_FOUND.getMessage()));
    }



}
