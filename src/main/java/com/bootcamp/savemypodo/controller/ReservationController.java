package com.bootcamp.savemypodo.controller;


import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.entity.Reservation;
import com.bootcamp.savemypodo.repository.MusicalRepository;
import com.bootcamp.savemypodo.repository.ReservationRepository;

import com.bootcamp.savemypodo.service.ReservationService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final MusicalRepository musicalRepository;

    // 예매 등록
    @PostMapping("/api/musicals/{musicalId}/seats")
    public ResponseEntity<?> createReservation(
            @PathVariable("musicalId") Long musicalId,
            @RequestBody ReservationRequest request,
            @AuthenticationPrincipal User user
    ) {
        reservationService.createReservation(user, musicalId, request.getSid());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ReservationResponse("성공적으로 예약이 되었습니다."));
    }

    //예매 취소
    @DeleteMapping("/api/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> cancelReservation(
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
        return ResponseEntity.ok(new ReservationResponse("성공적으로 취소 되었습니다."));
    }

    @Data
    static class ReservationRequest {
        private String sid; // 좌석 ID
    }

    @Data
    static class ReservationResponse {
        private final String message;
    }
}
