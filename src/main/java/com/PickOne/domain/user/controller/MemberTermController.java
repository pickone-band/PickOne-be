package com.PickOne.domain.user.controller;

import com.PickOne.domain.user.dto.MemberTermDto;
import com.PickOne.domain.user.dto.MemberTermDto.*;
import com.PickOne.domain.user.service.MemberTermService;
import com.PickOne.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 회원 약관 동의 내역 관리 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/member-terms")
public class MemberTermController {

    private final MemberTermService memberTermService;

    /**
     * 전체 MemberTerm 조회
     */
    @GetMapping
    public ResponseEntity<BaseResponse<List<MemberTermResponseDto>>> getAllMemberTerms() {
        List<MemberTermResponseDto> result = memberTermService.getAllMemberTerms();
        return BaseResponse.success(result);
    }

    /**
     * 특정 MemberTerm 단건 조회
     */
    @GetMapping("/{memberTermId}")
    public ResponseEntity<BaseResponse<MemberTermResponseDto>> getMemberTerm(@PathVariable Long memberTermId) {
        MemberTermResponseDto result = memberTermService.getMemberTerm(memberTermId);
        return BaseResponse.success(result);
    }

    /**
     * 특정 회원(Member)의 약관 동의 내역 조회
     */
    @GetMapping("/by-member/{memberId}")
    public ResponseEntity<BaseResponse<List<MemberTermResponseDto>>> getMemberTermsByMember(@PathVariable Long memberId) {
        List<MemberTermResponseDto> result = memberTermService.getMemberTermsByMember(memberId);
        return BaseResponse.success(result);
    }

    /**
     * 특정 약관(Term)에 동의한 회원 목록 조회
     */
    @GetMapping("/by-term/{termId}")
    public ResponseEntity<BaseResponse<List<MemberTermResponseDto>>> getMemberTermsByTerm(@PathVariable Long termId) {
        List<MemberTermResponseDto> result = memberTermService.getMemberTermsByTerm(termId);
        return BaseResponse.success(result);
    }
}
