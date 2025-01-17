package com.PickOne;

import com.PickOne.domain.user.model.entity.Member;
import com.PickOne.domain.user.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PickOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(PickOneApplication.class, args);
    }

    @Bean
    public CommandLineRunner initTestUsers(MemberRepository memberRepository) {
        return args -> {
            createUserIfNotExists(memberRepository, "testUser", "password123", "Test User", "testuser@example.com", "Tester");
            createUserIfNotExists(memberRepository, "anotherUser", "password456", "Another User", "anotheruser@example.com", "AnotherTester");
        };
    }

    private void createUserIfNotExists(MemberRepository memberRepository, String loginId, String password, String username, String email, String nickname) {
        if (memberRepository.findByLoginId(loginId).isEmpty()) {
            Member newUser = Member.builder()
                    .loginId(loginId)
                    .password(password) // 기본 비밀번호 설정 (암호화 고려 필요)
                    .username(username)
                    .email(email)
                    .nickname(nickname)
                    .build();
            memberRepository.save(newUser);
            System.out.println("User created: " + loginId);
        } else {
            System.out.println("User already exists: " + loginId);
        }
    }
}
