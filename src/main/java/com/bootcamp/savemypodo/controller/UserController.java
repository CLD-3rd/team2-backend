package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.dto.user.UserResponse;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user,
                                    HttpServletResponse response) {
        // 1. RefreshToken DB 삭제
        user.updateRefreshToken(null);
        userRepository.save(user);

        // 2. 클라이언트 쿠키 삭제 (Set-Cookie로 빈 값 전달)
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0); // 즉시 만료

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 즉시 만료

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok().body("로그아웃 완료");
    }

    @GetMapping("/me")
    public UserResponse getMyInfo(@AuthenticationPrincipal User user) {
        return new UserResponse(user.getEmail(), user.getNickname(), user.getRole().name());
    }
}
