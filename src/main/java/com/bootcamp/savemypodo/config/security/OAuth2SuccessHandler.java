package com.bootcamp.savemypodo.config.security;

import com.bootcamp.savemypodo.config.jwt.JwtTokenProvider;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.global.exception.ErrorCode;
import com.bootcamp.savemypodo.global.exception.UserException;
import com.bootcamp.savemypodo.repository.UserRepository;
import com.bootcamp.savemypodo.entity.CustomOAuth2User;
import com.bootcamp.savemypodo.service.RedisRefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisRefreshTokenService redisRefreshTokenService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        log.info("✅ [OAuth2 Success] 사용자 인증 성공: {}", email);
        
        // ✅ JWT 발급
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        // ✅ RefreshToken 저장 (Redis)
        redisRefreshTokenService.save(user.getId(), refreshToken);

        log.info("✅ [OAuth2 Success] RefreshToken 저장 성공");

        // ✅ JWT 쿠키로 전달 (보안용으로 HttpOnly 설정 추천)
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge((int) (jwtTokenProvider.getAccessTokenValidity() / 1000)); // milliseconds to seconds

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) (jwtTokenProvider.getRefreshTokenValidity() / 1000));

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        // ✅ 리디렉션
        response.sendRedirect(baseUrl); // 또는 프론트 주소로 redirect (ex. http://localhost:3000)
    }
}