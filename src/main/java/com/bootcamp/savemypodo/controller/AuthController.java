package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.config.jwt.JwtTokenProvider;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.global.exception.ErrorCode;
import com.bootcamp.savemypodo.global.exception.UserException;
import com.bootcamp.savemypodo.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getTokenFromCookie(request, "refreshToken");

        if (refreshToken == null) {
            throw new UserException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UserException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new UserException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        // 🔄 새 AccessToken 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(user);

        Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge((int) (jwtTokenProvider.getAccessTokenValidity() / 1000));
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok().build();
    }

    private String getTokenFromCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}