//package com.PickOne.domain.user.controller;
//
//import com.PickOne.domain.user.controller.ProfileController;
//import com.PickOne.domain.user.dto.ProfileDto.*;
//import com.PickOne.domain.user.service.ProfileService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.FilterType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//
//@WebMvcTest(
//        controllers = ProfileController.class,
//        excludeFilters = {
//                @ComponentScan.Filter(
//                        type = FilterType.ASSIGNABLE_TYPE
//                )
//        }
//)
//@AutoConfigureMockMvc(addFilters = false)
//class ProfileControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ProfileService profileService;
//
//    @Test
//    void testGetProfile() throws Exception {
//        when(profileService.getProfile(1L)).thenReturn(new ProfileResponseDto());
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/profiles/1"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    @Test
//    void testUpdateProfile() throws Exception {
//        ProfileUpdateDto updateDto = new ProfileUpdateDto();
//        when(profileService.updateProfile(eq(1L), any())).thenReturn(new ProfileResponseDto());
//        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/profiles/1")
//                        .contentType("application/json")
//                        .content("{}"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//}
