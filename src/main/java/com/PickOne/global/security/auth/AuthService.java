package com.PickOne.global.security.auth;

import com.PickOne.domain.user.model.Member;
import com.PickOne.domain.user.repository.MemberRepository;
import com.PickOne.global.dto.TokenResponse;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final BlacklistTokenRepository blacklistTokenRepository;

    /**
     * 🔹 로그인 로직
     */
    @Transactional
    public TokenResponse login(String loginId, String password) {
        log.info("🔍 로그인 요청: loginId={}", loginId);

        // 1. 사용자 조회
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                    log.error("❌ 로그인 실패 - 존재하지 않는 사용자: {}", loginId);
                    return new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
                });

        // 2. 비밀번호 검증 (암호화된 값과 비교)
        log.info("🔑 입력된 비밀번호: {}, 저장된 비밀번호: {}", password, member.getPassword());
        if (!passwordEncoder.matches(password, member.getPassword())) {
            log.error("❌ 비밀번호 불일치 - loginId={}", loginId);
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. JWT 토큰 발급
        String accessToken = jwtTokenService.createAccessToken(member.getLoginId());
        String refreshToken = jwtTokenService.createRefreshToken(member.getLoginId());

        log.info("✅ JWT 발급 완료 - AccessToken={}, RefreshToken={}", accessToken, refreshToken);

        // 4. 기존 토큰 삭제 후 새 토큰 저장
        jwtTokenService.deleteTokensByUserId(member.getLoginId());
        jwtTokenService.saveToken(
                member.getLoginId(),
                accessToken,
                refreshToken,
                LocalDateTime.now().plusDays(7) // 7일간 유효
        );

        // 5. 토큰 응답 반환
        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * 🔹 로그아웃 로직
     */
    @Transactional
    public void logout(String token) {
        log.info("🔍 로그아웃 요청 - Token={}", token);

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        JwtTokenEntity tokenEntity = jwtTokenService.getTokenByAccessToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        jwtTokenService.deleteTokensByUserId(tokenEntity.getUserId());

        BlacklistToken blacklistToken = BlacklistToken.builder()
                .token(token)
                .expiresAt(tokenEntity.getExpiresAt())
                .build();

        blacklistTokenRepository.save(blacklistToken);
    }

    /**
     * 🔹 Refresh Token을 이용한 Access Token 재발급
     */
    @Transactional
    public TokenResponse refreshAccessToken(String refreshToken) {
        if (!jwtTokenService.validateRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        JwtTokenEntity tokenEntity = jwtTokenService.getTokenByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        String newAccessToken = jwtTokenService.createAccessToken(tokenEntity.getUserId());

        return new TokenResponse(newAccessToken, refreshToken);
    }

}
