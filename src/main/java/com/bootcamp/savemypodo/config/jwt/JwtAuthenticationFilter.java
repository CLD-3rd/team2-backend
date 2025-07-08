package com.bootcamp.savemypodo.config.jwt;

import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.global.exception.ErrorCode;
import com.bootcamp.savemypodo.global.exception.UserException;
import com.bootcamp.savemypodo.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = getTokenFromCookie(request, "accessToken");
        String refreshToken = getTokenFromCookie(request, "refreshToken");

        log.debug("🔍 [JWT 필터] 요청 URI: {}", request.getRequestURI());
        log.debug("🔑 accessToken 존재 여부: {}", accessToken != null);
        log.debug("🔑 refreshToken 존재 여부: {}", refreshToken != null);

        try {
            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                // Access Token 유효
                log.info("✅ Access Token 유효: 인증 처리 시작");
                setAuthenticationFromAccessToken(accessToken, request);

            } else if (refreshToken != null) {
                // Access Token 만료 or 없음 → Refresh Token 검사
                log.warn("⚠️ Access Token 만료 또는 없음, Refresh Token으로 인증 시도");

                String email = jwtTokenProvider.getEmailFromToken(refreshToken);
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

                // Refresh Token 만료 여부 확인
                if (jwtTokenProvider.isTokenExpired(refreshToken)) {
                    log.warn("❌ Refresh Token 만료됨: {}", email);
                    user.updateRefreshToken(null);
                    userRepository.save(user);
                    throw new UserException(ErrorCode.REFRESH_TOKEN_EXPIRED);
                }

                // Refresh Token 불일치
                if (!refreshToken.equals(user.getRefreshToken())) {
                    log.warn("❌ Refresh Token 불일치: {}", email);
                    throw new UserException(ErrorCode.REFRESH_TOKEN_MISMATCH);
                }

                // 새로운 Access Token 발급
                String newAccessToken = jwtTokenProvider.createAccessToken(user);
                Cookie newAccessTokenCookie = new Cookie("accessToken", newAccessToken);
                newAccessTokenCookie.setHttpOnly(true);
                newAccessTokenCookie.setPath("/");
                newAccessTokenCookie.setMaxAge((int) (jwtTokenProvider.getAccessTokenValidity() / 1000));
                response.addCookie(newAccessTokenCookie);

                setAuthenticationFromAccessToken(newAccessToken, request);
                log.info("🔄 Access Token 재발급 완료 for user: {}", email);

            } else {
                log.warn("❗ 유효한 토큰이 존재하지 않음");
                throw new UserException(ErrorCode.INVALID_TOKEN);
            }

        } catch (UserException e) {
            log.error("❌ 인증 실패: {}", e.getErrorCode().getMessage());
            throw e; // GlobalExceptionHandler로 전파
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private void setAuthenticationFromAccessToken(String token, HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getRole().getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("🔐 사용자 인증 성공: {}", email);
    }
}