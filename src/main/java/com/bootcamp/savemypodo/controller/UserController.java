package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.dto.reservation.MyReservationResponse;
import com.bootcamp.savemypodo.dto.user.UserResponse;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.repository.UserRepository;
import com.bootcamp.savemypodo.service.RedisRefreshTokenService;
import com.bootcamp.savemypodo.service.ReservationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final RedisRefreshTokenService redisRefreshTokenService;
    private final ReservationService reservationService;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        log.info("🚪 [Logout Request] 사용자 로그아웃 요청: {}", user.getEmail());
        // 1. Redis에서 RefreshToken 삭제
        redisRefreshTokenService.deleteRefreshToken(user.getId().toString());
        log.info("[Logout] Redis에서 RefreshToken 삭제 완료: userId={}", user.getId());

        // 2. 클라이언트 쿠키 삭제 (Set-Cookie로 빈 값 전달)
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0); // 즉시 만료

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 즉시 만료

        Cookie jsessionidCookie = new Cookie("JSESSIONID", null);
        jsessionidCookie.setHttpOnly(true);
        jsessionidCookie.setPath("/");
        jsessionidCookie.setMaxAge(0); // 즉시 만료

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        response.addCookie(jsessionidCookie);

        // 4. 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }


        return ResponseEntity.ok().body("로그아웃 완료");
    }

    @GetMapping("/me")
    public UserResponse getMyInfo(@AuthenticationPrincipal User user) {
        return new UserResponse(user.getEmail(), user.getNickname(), user.getRole().name());
    }

    @GetMapping("/my-reservations")
    public ResponseEntity<List<MyReservationResponse>> getMyReservations(@AuthenticationPrincipal User user) {
        List<MyReservationResponse> myReservations = reservationService.getMyReservationsByUser(user);
        return ResponseEntity.ok(myReservations);
    }

}
