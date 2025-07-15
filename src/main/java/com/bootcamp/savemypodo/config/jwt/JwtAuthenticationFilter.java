package com.bootcamp.savemypodo.config.jwt;

import com.bootcamp.savemypodo.config.security.utils.CookieUtil;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CookieUtil cookieUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 여기 경로는 Jwt Filter 무시
        if (uri.equals("/") || uri.startsWith("/login") || uri.equals("/api/musicals") || uri.equals("/actuator/prometheus") || uri.equals("/api/user/me")) {
            filterChain.doFilter(request, response); // 그냥 통과
            return;
        }

        String accessToken = cookieUtil.getTokenFromCookie(request, "accessToken");
        String refreshToken = cookieUtil.getTokenFromCookie(request, "refreshToken");

        log.info("🔍 [JWT 필터] 요청 URI: {}", uri);

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
                    throw new UserException(ErrorCode.REFRESH_TOKEN_EXPIRED);
                }

                // Refresh Token 불일치
                if (!refreshToken.equals(user.getRefreshToken())) {
                    log.warn("❌ Refresh Token 불일치: {}", email);
                    throw new UserException(ErrorCode.REFRESH_TOKEN_MISMATCH);
                }
                // 자동 재발급 제거 시
//                log.warn("⚠️ Access Token 만료 → 자동 재발급 생략됨");
//                throw new UserException(ErrorCode.ACCESS_TOKEN_EXPIRED);

                // 새로운 Access Token 발급
                String newAccessToken = jwtTokenProvider.createAccessToken(user);

                Cookie newAccessTokenCookie = cookieUtil.createCookie("accessToken", newAccessToken);

                response.addCookie(newAccessTokenCookie);

                setAuthenticationFromAccessToken(newAccessToken, request);
                log.info("🔄 Access Token 재발급 완료 for user: {}", email);
                // 수정한 부분
            } else {
                log.info("🔒 토큰 없음—익명 사용자로 진행");
                filterChain.doFilter(request, response);
                return;
            }

        } catch (UserException e) {
            log.warn("🚫 [JWT Filter] UserException 발생 - {}: {}", e.getErrorCode(), e.getMessage());
            setErrorResponse(response, e.getErrorCode(), request.getRequestURI());
            return; // ❗ 더 이상 필터 체인을 진행하지 않음
        }

        filterChain.doFilter(request, response);
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

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode, String path) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String body = String.format("""
                {
                  "status": %d,
                  "error": "%s",
                  "path": "%s"
                }
                """, errorCode.getStatus().value(), errorCode.getMessage(), path);

        response.getWriter().write(body);
    }
}