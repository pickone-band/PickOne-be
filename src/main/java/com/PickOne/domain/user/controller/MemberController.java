//package com.PickOne.domain.user.controller;
//
//import com.PickOne.domain.user.dto.MemberDto.*;
//import com.PickOne.domain.user.service.MemberService;
//import com.PickOne.global.exception.SuccessCode;
//import com.PickOne.global.exception.BaseResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/members")
//@RequiredArgsConstructor
//public class MemberController {
//
//    private final MemberService memberService;
//
//    @PostMapping
//    public ResponseEntity<BaseResponse<Void>> signUp(@RequestBody MemberCreateDto dto) {
//        memberService.createMember(dto);
//        System.out.println(dto.getNickname()+dto.getUsername());
//        return BaseResponse.success(SuccessCode.CREATED);
//    }
//
//    @GetMapping
//    public ResponseEntity<BaseResponse<List<MemberResponseDto>>> getAll() {
//        List<MemberResponseDto> list = memberService.getAllMembers();
//        return BaseResponse.success(SuccessCode.OK, list);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<BaseResponse<MemberResponseDto>> getOne(@PathVariable Long id) {
//        MemberResponseDto dto = memberService.getMember(id);
//        return BaseResponse.success(SuccessCode.OK, dto);
//    }
//
//
//    @PatchMapping("/{id}")
//    public ResponseEntity<BaseResponse<MemberResponseDto>> update(@PathVariable Long id,
//                                                                  @RequestBody MemberUpdateDto dto) {
//        MemberResponseDto updated = memberService.updateMember(id, dto);
//        return BaseResponse.success(SuccessCode.UPDATED);
//    }
//
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
//        memberService.deleteMember(id);
//        return BaseResponse.success(SuccessCode.DELETED);
//    }
//
//}
