//package com.PickOne.domain.user.controller;
//
//import com.PickOne.domain.user.dto.MemberStateDto.*;
//import com.PickOne.domain.user.service.MemberStateService;
//import com.PickOne.global.exception.BaseResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/member-states")
//@RequiredArgsConstructor
//public class MemberStateController {
//
//    private final MemberStateService memberStateService;
//
//    @GetMapping
//    public ResponseEntity<BaseResponse<List<MemberStateResponseDto>>> getAll() {
//        List<MemberStateResponseDto> list = memberStateService.getAllStates();
//        return BaseResponse.success(list);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<BaseResponse<MemberStateResponseDto>> getOne(@PathVariable Long id) {
//        MemberStateResponseDto dto = memberStateService.getMemberState(id);
//        return BaseResponse.success(dto);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<BaseResponse<MemberStateResponseDto>> update(@PathVariable Long id,
//                                                                       @RequestBody MemberStateUpdateDto dto) {
//        dto.setId(id); // Ensure ID consistency
//        MemberStateResponseDto updated = memberStateService.updateMemberState(dto);
//        return BaseResponse.success(updated);
//    }
//}