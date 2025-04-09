package com.PickOne.domain.application.model.model.entity;

import lombok.Getter;

@Getter
public enum Status {
    PENDING, // 대기
    APPROVED, // 승인
    REJECTED // 거절
}
