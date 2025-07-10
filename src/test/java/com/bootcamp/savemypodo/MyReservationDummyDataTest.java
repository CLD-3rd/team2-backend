package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.Reservation;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.repository.MusicalRepository;
import com.bootcamp.savemypodo.repository.ReservationRepository;
import com.bootcamp.savemypodo.repository.SeatRepository;
import com.bootcamp.savemypodo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class MyReservationDummyDataTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MusicalRepository musicalRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private User user1;

    @BeforeEach
    void setUp() {
        // 이미 존재하는 UID=1 유저 조회
        user1 = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User with ID 1 not found."));

        for (int i = 1; i <= 3; i++) {
            // 뮤지컬 생성
            Musical musical = new Musical();
            musical.setTitle("뮤지컬 " + i);
            musical.setPosterUrl("/images/musical" + i + ".jpg");
            musical.setStartTime(LocalTime.of(14 + i, 0));
            musical.setEndTime(LocalTime.of(16 + i, 30));
            musical.setDate(LocalDate.of(2025, 8, i));
            musical.setDescription("설명 " + i);
            musical.setPrice(70000L + (i * 5000));
            musical.setLocation("서울극장 " + i);
            musical.setDuration(120L + (i * 10));
            musical.setReservedCount(0L);
            musicalRepository.save(musical);

            // 좌석 생성
            Seat seat = new Seat();
            seat.setMusical(musical);
            seat.setRow((char) ('A' + i));
            seat.setColumn(i);
            seatRepository.save(seat);

            // 예약 생성
            Reservation reservation = new Reservation();
            reservation.setUser(user1);
            reservation.setMusical(musical);
            reservation.setSeat(seat);
            reservationRepository.save(reservation);
        }
    }

    @Test
    void 유저1번의_예약_3개_확인() {
        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(3);
        reservations.forEach(res -> {
            assertThat(res.getUser().getId()).isEqualTo(1L);
        });
    }
}