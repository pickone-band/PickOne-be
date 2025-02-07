    package com.PickOne;

    import com.PickOne.domain.user.model.Member;
    import com.PickOne.domain.user.repository.MemberRepository;
    import org.springframework.boot.CommandLineRunner;
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Profile;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.transaction.annotation.Transactional;

    @SpringBootApplication
    public class PickOneApplication {

        public static void main(String[] args) {
            SpringApplication.run(PickOneApplication.class, args);
        }

        @Bean
        @Profile("dev")
        @Transactional
        public CommandLineRunner initTestUser(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
            return args -> {
                if (memberRepository.findByLoginId("testUser").isEmpty()) {

                    String googleEmail = "heachangchoi@gmail.com";
                    String defaultPassword = "password123"; // 기존 기본 비밀번호
                    String encodedPassword = passwordEncoder.encode(defaultPassword); // 비밀번호 암호화

                    // 이미 계정이 존재하는지 확인
                    if (memberRepository.findByEmail(googleEmail).isEmpty()) {
                        Member testUser = Member.builder()
                                .loginId("TestUser") // Google 이메일을 로그인 ID로 사용
                                .password(encodedPassword) // 암호화된 비밀번호 저장
                                .username("TestUser") // 기존 사용자명 유지
                                .email(googleEmail) // Google 이메일
                                .nickname("Tester") // 기존 닉네임 유지
                                .build();
                        memberRepository.save(testUser);
                        System.out.println("✅ Test user created: " + testUser.getLoginId());
                    } else {
                        System.out.println("⚡ Test user already exists.");
                    }
                }
            };
        }
    }