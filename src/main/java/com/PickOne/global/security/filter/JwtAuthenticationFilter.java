package com.PickOne.global.security.filter;

import com.PickOne.global.security.auth.BlacklistTokenRepository;
import com.PickOne.global.security.auth.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final BlacklistTokenRepository blacklistTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Swagger 리소스 요청은 필터를 건너뜀
        if (uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("JwtAuthenticationFilter 실행 - URI: {}", request.getRequestURI());
        // 요청에서 JWT 토큰 추출
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && !token.isEmpty() && jwtTokenProvider.validateToken(token)) {
            // ✅ getAuthentication에서 UserDetails 직접 조회하도록 변경
            SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthentication(token));
        }

        if (blacklistTokenRepository.existsByToken(token)) {
            log.warn("🚫 블랙리스트에 등록된 토큰: {}", token);
            filterChain.doFilter(request, response);
            return;
        }


        filterChain.doFilter(request, response);
    }
}
