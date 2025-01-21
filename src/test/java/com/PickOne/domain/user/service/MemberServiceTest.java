package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.MemberDto.*;
import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }
    @Test
    @DisplayName("회원 가입 - 중복 LoginId")
    void createMember_duplicateLoginId() {
        // given
        MemberCreateDto dto1 = new MemberCreateDto();
        dto1.setLoginId("testUser");
        dto1.setPassword("1111");
        dto1.setUsername("유저A");
        dto1.setEmail("a@test.com");
        dto1.setNickname("nickA");
        memberService.createMember(dto1);

        // when
        MemberCreateDto dto2 = new MemberCreateDto();
        dto2.setLoginId("testUser"); // 중복
        dto2.setPassword("2222");
        dto2.setUsername("유저B");
        dto2.setEmail("b@test.com");
        dto2.setNickname("nickB");

        // then
        assertThatThrownBy(() -> memberService.createMember(dto2))
                .isInstanceOf(BusinessException.class).hasMessageContaining(ErrorCode.DUPLICATE_LOGIN_ID.getMessage());

    }

    @Test
    @DisplayName("회원 가입 - 성공")
    void createMember_success() {
        // given
        MemberCreateDto dto = new MemberCreateDto();
        dto.setLoginId("newUser");
        dto.setPassword("pass");
        dto.setUsername("새유저");
        dto.setEmail("new@test.com");
        dto.setNickname("newNick");

        // when
        memberService.createMember(dto);

        // then
        Member found = memberRepository.findByLoginId("newUser")
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않음"));
        assertThat(found).isNotNull();
        assertThat(found.getLoginId()).isEqualTo("newUser");

        // 비밀번호 암호화 검사
        assertThat(passwordEncoder.matches("pass", found.getPassword())).isTrue();
    }

    @Test
    @DisplayName("회원 조회 - 전체")
    void testGetAllMembers() {
        // given
        Member member1 = Member.builder()
                .loginId("user1")
                .password("pass1")
                .username("유저1")
                .email("user1@test.com")
                .nickname("nick1")
                .build();

        Member member2 = Member.builder()
                .loginId("user2")
                .password("pass2")
                .username("유저2")
                .email("user2@test.com")
                .nickname("nick2")
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<MemberResponseDto> result = memberService.getAllMembers();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("username").containsExactly("유저1", "유저2");
    }

    @Test
    @DisplayName("회원 조회 - 단일")
    void testGetMember_Success() {
        // given
        Member member = Member.builder()
                .loginId("user1")
                .password("pass1")
                .username("유저1")
                .email("user1@test.com")
                .nickname("nick1")
                .build();

        Member savedMember = memberRepository.save(member);

        // when
        MemberResponseDto result = memberService.getMember(savedMember.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("유저1");
        assertThat(result.getEmail()).isEqualTo("user1@test.com");
    }

    @Test
    @DisplayName("회원 조회 - 실패")
    void testGetMember_NotFound() {
        // given
        Long invalidId = 999L;

        // when & then
        assertThatThrownBy(() -> memberService.getMember(invalidId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_INFO_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원 정보 업데이트 성공 테스트")
    void updateMember_Success() {
        // given
        Long memberId = 1L;
        Member member = Member.builder()
                .loginId("user1")
                .password("password")
                .username("기존유저")
                .nickname("기존닉네임")
                .email("user1@test.com")
                .build();
        memberRepository.save(member);

        MemberUpdateDto updateDto = new MemberUpdateDto();
        updateDto.setUsername("업데이트유저");
        updateDto.setNickname("업데이트닉네임");

        // when
        MemberResponseDto result = memberService.updateMember(member.getId(), updateDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("업데이트유저");
        assertThat(result.getNickname()).isEqualTo("업데이트닉네임");

        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updatedMember.getUsername()).isEqualTo("업데이트유저");
        assertThat(updatedMember.getNickname()).isEqualTo("업데이트닉네임");
    }

    @Test
    @DisplayName("회원 정보 업데이트 실패 - 닉네임 중복")
    void updateMember_DuplicateNickname() {
        // given
        Member member1 = Member.builder()
                .loginId("user1")
                .password("password")
                .username("유저1")
                .nickname("닉네임1")
                .email("user1@test.com")
                .build();
        memberRepository.save(member1);

        Member member2 = Member.builder()
                .loginId("user2")
                .password("password")
                .username("유저2")
                .nickname("닉네임2")
                .email("user2@test.com")
                .build();
        memberRepository.save(member2);

        MemberUpdateDto updateDto = new MemberUpdateDto();
        updateDto.setUsername("유저1-업데이트");
        updateDto.setNickname("닉네임2");

        // when & then
        assertThatThrownBy(() -> memberService.updateMember(member1.getId(), updateDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.DUPLICATE_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("회원 정보 업데이트 실패 - 회원 없음")
    void updateMember_NotFound() {
        // given
        Long invalidId = 999L;
        MemberUpdateDto updateDto = new MemberUpdateDto();
        updateDto.setUsername("유저1-업데이트");
        updateDto.setNickname("닉네임1");

        // when & then
        assertThatThrownBy(() -> memberService.updateMember(invalidId, updateDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_INFO_NOT_FOUND.getMessage());
    }
  
  @Test
  @DisplayName("회원 삭제 - 성공")
    void deleteMember_success() {
        // given
        Member member = Member.builder()
                .loginId("user1")
                .password("pass1")
                .username("유저1")
                .email("user1@test.com")
                .nickname("nick1")
                .build();

        Member savedMember = memberRepository.save(member);

        // when
        memberService.deleteMember(savedMember.getId());

        // then
        Optional<Member> deletedMember = memberRepository.findById(savedMember.getId());
        assertThat(deletedMember).isEmpty();
    }

    @Test
    @DisplayName("회원 삭제 - 실패 (존재하지 않는 회원)")
    void deleteMember_notFound() {
        // given
        Long invalidId = 999L;

        // when & then
        assertThatThrownBy(() -> memberService.deleteMember(invalidId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_INFO_NOT_FOUND.getMessage());
    }

}

