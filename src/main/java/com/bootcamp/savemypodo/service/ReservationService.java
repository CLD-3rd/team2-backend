package com.bootcamp.savemypodo.service;

import org.springframework.stereotype.Service;

import com.bootcamp.savemypodo.dto.reservation.ReservationRequestDto;
import com.bootcamp.savemypodo.entity.Performance;
import com.bootcamp.savemypodo.entity.Reservation;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.repository.PerformanceRepository;
import com.bootcamp.savemypodo.repository.ReservationRepository;
import com.bootcamp.savemypodo.repository.SeatRepository;
import com.bootcamp.savemypodo.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;

    @Transactional
    public void reserveSeat(ReservationRequestDto request) {
        // 1. 좌석 찾기
        Seat seat = seatRepository.findByPerformance_PidAndSid(request.pid(), request.sid())
                .orElseThrow(() -> new RuntimeException("해당 좌석을 찾을 수 없습니다."));
        
        // 2. 이미 예약됨 확인
        if (Boolean.TRUE.equals(seat.getSeatStatus())) {
            throw new IllegalStateException("이미 예약된 좌석입니다.");
        }

        // 3. 사용자 / 공연 조회
        User user = userRepository.findById(request.uid())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Performance performance = performanceRepository.findById(request.pid())
                .orElseThrow(() -> new RuntimeException("공연 정보를 찾을 수 없습니다."));

        // 4. 좌석 상태 업데이트
        seat.setSeatStatus(true);

        // 5. 예약 객체 저장
        Reservation reservation = Reservation.builder()
                .user(user)
                .performance(performance)
                .sid(request.sid())
                .build();

        reservationRepository.save(reservation);
    }
}
