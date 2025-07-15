package com.bootcamp.savemypodo.config.security.utils;

import com.bootcamp.savemypodo.config.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final JwtTokenProvider jwtTokenProvider;

    // ✅ 공통 쿠키 생성 유틸
    public Cookie createCookie(String type, String token) {
        long expirationTime;

        if (type.equals("accessToken")) {
            expirationTime = jwtTokenProvider.getAccessTokenValidity();
        } else {
            expirationTime = jwtTokenProvider.getRefreshTokenValidity();
        }

        Cookie cookie = new Cookie(type, token);

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (expirationTime / 1000));
//        cookie.setDomain(".savemypodo.shop"); // 하위 도메인까지 허용
        cookie.setAttribute("SameSite", "None"); // CORS 허용

        return cookie;
    }

    // ✅ 쿠키 삭제용 유틸
    public Cookie deleteCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "None");
        return cookie;
    }

    public String getTokenFromCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
