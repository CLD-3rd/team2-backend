package com.bootcamp.savemypodo.config.security;

import com.bootcamp.savemypodo.config.jwt.JwtTokenProvider;
import com.bootcamp.savemypodo.config.security.utils.CookieUtil;
import com.bootcamp.savemypodo.entity.CustomOAuth2User;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.global.exception.ErrorCode;
import com.bootcamp.savemypodo.global.exception.UserException;
import com.bootcamp.savemypodo.repository.UserRepository;
import com.bootcamp.savemypodo.service.redis.RedisRefreshTokenService;
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
    private final CookieUtil cookieUtil;

    @Value("${frontend.url}")
    private String frontendUrl;

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

        setCookiesForProduction(response, accessToken, refreshToken);
        log.info("✅ [OAuth2 Success] Token 전송 완료");

        // ✅ 리디렉션
        response.sendRedirect(frontendUrl); // 프론트 주소로 redirect (ex. http://localhost:3000)
    }

    private void setCookiesForProduction(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessTokenCookie = cookieUtil.createCookie("accessToken", accessToken);
        Cookie refreshTokenCookie = cookieUtil.createCookie("refreshToken", refreshToken);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    private void setCookiesForLocalTest(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setMaxAge((int) (jwtTokenProvider.getAccessTokenValidity() / 1000)); // milliseconds to seconds
        accessTokenCookie.setDomain("localhost"); // 생략 가능
        accessTokenCookie.setAttribute("SameSite", "Lax");

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge((int) (jwtTokenProvider.getRefreshTokenValidity() / 1000)); // milliseconds to seconds
        refreshTokenCookie.setDomain("localhost"); // 생략 가능
        refreshTokenCookie.setAttribute("SameSite", "Lax");

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}