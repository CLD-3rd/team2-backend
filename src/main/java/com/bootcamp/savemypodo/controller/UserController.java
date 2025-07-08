package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.dto.user.UserResponse;
import com.bootcamp.savemypodo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    @GetMapping("/me")
    public UserResponse getMyInfo(@AuthenticationPrincipal User user) {
        return new UserResponse(user.getEmail(), user.getNickname(), user.getRole().name());
    }
}
