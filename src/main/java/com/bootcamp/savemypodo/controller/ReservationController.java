package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.dto.reservation.ReservationResponseDto;
import com.bootcamp.savemypodo.repository.PerformanceRepository;
import com.bootcamp.savemypodo.repository.ReservationRepository;
//import com.bootcamp.savemypodo.security.CustomUserPrincipal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final PerformanceRepository performanceRepository;

    // 예매 취소
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long mid,
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

        boolean exists = reservationRepository.existsByUser_IdAndMusical_Mid(userId, mid);
        if (!exists) {
            return ResponseEntity.status(404).build(); // 예매 기록 없음
        }

        reservationRepository.deleteByUser_IdAndMusical_Mid(userId, mid);
        return ResponseEntity.noContent().build(); // 성공적으로 삭제됨
    }
}

