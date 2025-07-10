package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.entity.Reservation;
import com.bootcamp.savemypodo.service.ReservationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import com.bootcamp.savemypodo.entity.User;

@RestController
@RequestMapping("/api/musicals")
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationService reservationService;
	
    @PostMapping("/{musicalId}/seats")
    public ResponseEntity<?> createReservation(
            @PathVariable("musicalId") Long musicalId,
            @RequestBody ReservationRequest request,
            @AuthenticationPrincipal User user
    ) {
        reservationService.createReservation(user, musicalId,request.getSid());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ReservationResponse("성공적으로 예약이 되었습니다."));
    }

    @Data
    static class ReservationRequest {
        private String sid;      // 좌석 ID
    }

    @Data
    static class ReservationResponse {
        private final String message;
    }
}
