package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.config.jwt.JwtTokenProvider;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.global.exception.ErrorCode;
import com.bootcamp.savemypodo.global.exception.UserException;
import com.bootcamp.savemypodo.repository.UserRepository;
import com.bootcamp.savemypodo.service.AuthService;
import com.bootcamp.savemypodo.service.UserService;
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

    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        Cookie accessTokenCookie = authService.generateRefreshToken(request);
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok().build();
    }
}