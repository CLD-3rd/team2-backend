package com.bootcamp.savemypodo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bootcamp.savemypodo.dto.reservation.ReservationRequestDto;
import com.bootcamp.savemypodo.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/performances/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> reserveSeat(@RequestBody ReservationRequestDto request) {
        try {
            reservationService.reserveSeat(request);
            return ResponseEntity.ok(new MessageResponse("예약이 완료되었습니다"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    public record MessageResponse(String message) {}
}