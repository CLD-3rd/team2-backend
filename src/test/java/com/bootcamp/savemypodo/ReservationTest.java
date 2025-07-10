package com.bootcamp.savemypodo;

import com.bootcamp.savemypodo.entity.*;
import com.bootcamp.savemypodo.global.enums.Provider;
import com.bootcamp.savemypodo.global.enums.Role;
import com.bootcamp.savemypodo.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class ReservationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    public void createDummyReservation() {
        // uid = 1 유저 조회
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("❌ 유저가 존재하지 않습니다."));

        for (int i = 1; i <= 3; i++) {
            // 공연 생성
            Performance performance = new Performance();
            performance.setTitle("공연 " + i);
            performance.setSummary("설명 " + i);
            performance.setDate(LocalDateTime.now().plusDays(i));
            performance.setPrice(50000 + i * 1000);
            performance.setSeatNumber(100);
            performance = performanceRepository.save(performance);

            // 좌석 생성
            Seat seat = new Seat();
            seat.setSid("A" + i); // 예: A1, A2, A3
            seat.setSeatStatus(true);
            seat.setPerformance(performance);
            seat = seatRepository.save(seat);

            // 예매 생성
            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setPerformance(performance);
            reservation.setSeat(seat);  // Seat도 연결
            reservationRepository.save(reservation);
        }

        System.out.println("✅ uid=1 유저의 예매 3건이 생성되었습니다.");
    }
}