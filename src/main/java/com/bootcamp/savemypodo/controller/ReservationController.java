package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.repository.MusicalRepository;
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
    private final MusicalRepository musicalRepository;

    // 예매 상태 조회
//    @GetMapping("/status")
//    public ResponseEntity<ReservationResponseDto> getReservationStatus(
//            @RequestParam Long pid,
//            Authentication authentication) {
//
//        Long userId = null;
//        if (authentication != null && authentication.isAuthenticated()) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof User user) {
//                userId = user.getId();
//            }
//        }
//
//        Musical performance = musicalRepository.findById(pid)
//                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다."));
//        // 전체 좌석 수
//        int seatNumber = performance.getSeatNumber();
//
//        // 예매된 좌석 수
//        int currentReserved = reservationRepository.countByPerformance_Pid(pid);
//        boolean soldOut = currentReserved >= performance.getSeatNumber();
//
//        boolean reserved = false;
//        if (userId != null) {
//            reserved = reservationRepository.existsByUser_IdAndPerformance_Pid(userId, pid);
//        }
//
//        return ResponseEntity.ok(new ReservationResponseDto(
//        		reserved,
//        		soldOut,
//        		seatNumber,
//        		currentReserved));
//    }

    // 예매 취소
//    @DeleteMapping("/{pid}")
//    public ResponseEntity<Void> cancelReservation(
//            @PathVariable Long pid,
//            Authentication authentication) {
//
//        Long userId = null;
//        if (authentication != null && authentication.isAuthenticated()) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof User user) {
//                userId = user.getId();
//            }
//        }
//
//        if (userId == null) {
//            return ResponseEntity.status(401).build(); // 인증 실패
//        }
//
//        boolean exists = reservationRepository.existsByUser_IdAndPerformance_Pid(userId, pid);
//        if (!exists) {
//            return ResponseEntity.status(404).build(); // 예매 기록 없음
//        }
//
//        reservationRepository.deleteByUser_IdAndPerformance_Pid(userId, pid);
//        return ResponseEntity.noContent().build(); // 성공적으로 삭제됨
//    }
}

