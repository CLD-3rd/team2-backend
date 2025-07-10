package com.bootcamp.savemypodo.controller;


import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.service.ReservationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예매 등록
    @PostMapping("/api/musicals/{musicalId}/seats")
    public ResponseEntity<?> createReservation(
            @PathVariable("musicalId") Long musicalId,
            @RequestBody ReservationRequest request,
            @AuthenticationPrincipal User user
    ) {
        reservationService.createReservation(user, musicalId, request.getSeatId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ReservationResponse("성공적으로 예약이 되었습니다."));
    }

    //예매 취소
    @DeleteMapping("/api/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable("reservationId") Long musicalId,
            @AuthenticationPrincipal User user) {

        reservationService.cancelReservation(user.getId(), musicalId);
        return ResponseEntity.ok(new ReservationResponse("성공적으로 취소 되었습니다."));
    }

    // -> 이것 보다는 dto 패키지에 ReservationRequest, Response를 사용하는것을 추천
    @Data
    static class ReservationRequest {
        private String seatId; // 좌석 ID
    }

    @Data
    static class ReservationResponse {
        private final String message;
    }

}
