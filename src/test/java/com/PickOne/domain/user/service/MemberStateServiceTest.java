package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.MemberStateDto.*;
import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.model.MemberState;
import com.PickOne.domain.user.model.MemberStatus;
import com.PickOne.domain.user.repository.MemberStateRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberStateServiceTest {

    @Mock
    private MemberStateRepository memberStateRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MemberStateService memberStateService;

    @Test
    @DisplayName("회원 상태를 BANNED로 변경")
    void updateMemberStateToBanned() {
        // given
        Member member = Member.builder().id(1L).build(); // 🔹 빌더 패턴 활용하여 객체 생성

        MemberState memberState = MemberState.builder()
                .id(1L)
                .member(member)
                .status(MemberStatus.ACTIVE)
                .build();

        MemberStateUpdateDto updateDto = new MemberStateUpdateDto();
        updateDto.setId(1L);
        updateDto.setStatus("BANNED");
        updateDto.setReason("Policy Violation");

        when(memberStateRepository.findById(1L)).thenReturn(Optional.of(memberState));
        when(memberStateRepository.save(any())).thenReturn(memberState);
        when(modelMapper.map(any(), eq(MemberStateResponseDto.class))).thenReturn(new MemberStateResponseDto());

        // when
        MemberStateResponseDto response = memberStateService.updateMemberState(updateDto);

        // then
        assertNotNull(response);
        verify(memberStateRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("이미 BANNED 상태일 때 예외 발생")
    void alreadyBannedException() {
        // given
        Member member = Member.builder().id(1L).build();

        MemberState memberState = MemberState.builder()
                .id(1L)
                .member(member)
                .status(MemberStatus.BANNED)
                .build();

        MemberStateUpdateDto updateDto = new MemberStateUpdateDto();
        updateDto.setId(1L);
        updateDto.setStatus("BANNED");

        when(memberStateRepository.findById(1L)).thenReturn(Optional.of(memberState));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> memberStateService.updateMemberState(updateDto));

        assertEquals(ErrorCode.ALREADY_BANNED, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원 상태 단건 조회")
    void getMemberStateById() {
        // given
        Member member = Member.builder().id(1L).build();
        MemberState memberState = MemberState.builder()
                .id(1L)
                .member(member)
                .status(MemberStatus.ACTIVE)
                .build();
        MemberStateResponseDto responseDto = new MemberStateResponseDto();
        responseDto.setId(1L);
        responseDto.setStatus("ACTIVE");

        when(memberStateRepository.findById(1L)).thenReturn(Optional.of(memberState));
        when(modelMapper.map(memberState, MemberStateResponseDto.class)).thenReturn(responseDto);

        // when
        MemberStateResponseDto result = memberStateService.getMemberState(1L);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    @DisplayName("회원 상태 전체 조회")
    void getAllMemberStates() {
        // given
        Member member1 = Member.builder().id(1L).build();
        Member member2 = Member.builder().id(2L).build();

        MemberState state1 = MemberState.builder().id(1L).member(member1).status(MemberStatus.ACTIVE).build();
        MemberState state2 = MemberState.builder().id(2L).member(member2).status(MemberStatus.BANNED).build();

        List<MemberState> memberStateList = Arrays.asList(state1, state2);
        when(memberStateRepository.findAll()).thenReturn(memberStateList);

        MemberStateResponseDto responseDto1 = new MemberStateResponseDto();
        responseDto1.setId(1L);
        responseDto1.setStatus("ACTIVE");

        MemberStateResponseDto responseDto2 = new MemberStateResponseDto();
        responseDto2.setId(2L);
        responseDto2.setStatus("BANNED");

        when(modelMapper.map(state1, MemberStateResponseDto.class)).thenReturn(responseDto1);
        when(modelMapper.map(state2, MemberStateResponseDto.class)).thenReturn(responseDto2);

        // when
        List<MemberStateResponseDto> result = memberStateService.getAllStates();

        // then
        assertEquals(2, result.size());
        assertEquals("ACTIVE", result.get(0).getStatus());
        assertEquals("BANNED", result.get(1).getStatus());
    }
}
