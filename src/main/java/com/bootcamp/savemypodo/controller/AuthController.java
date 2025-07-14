package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.service.auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        Cookie accessTokenCookie = authService.generateRefreshToken(request);
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok().build();
    }
}