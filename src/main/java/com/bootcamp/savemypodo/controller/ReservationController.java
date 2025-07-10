package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.User;

import com.bootcamp.savemypodo.service.ReservationService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable("reservationId") Musical musical,
            Authentication authentication) {

        Long userId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User user) {
                userId = user.getId();
            }
        }

        if (userId == null) {
            return ResponseEntity.status(401).build(); // 인증 실패
        }

        reservationService.cancelReservation(userId, musical.getId());
        return ResponseEntity.noContent().build();
    }
}
