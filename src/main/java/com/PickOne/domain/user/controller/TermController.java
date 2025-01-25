package com.PickOne.domain.user.controller;

import com.PickOne.domain.user.dto.TermDto.*;
import com.PickOne.domain.user.service.TermService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermController {

    private final TermService termService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createTerm(@Valid @RequestBody TermCreateDto dto) {
        termService.createTerm(dto);
        return BaseResponse.success(SuccessCode.CREATED);
    }


}
