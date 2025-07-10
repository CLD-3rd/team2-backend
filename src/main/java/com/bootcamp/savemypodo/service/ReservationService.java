package com.bootcamp.savemypodo.service;

import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.Reservation;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.repository.PerformanceRepository;
import com.bootcamp.savemypodo.repository.ReservationRepository;
import com.bootcamp.savemypodo.repository.SeatRepository;
import com.bootcamp.savemypodo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;

    public Reservation createReservation(User user, Long mid, String sid) {
    	Character row = sid.charAt(0);
        Integer column;
        try {
            column = Integer.parseInt(sid.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("좌석 번호가 숫자가 아닙니다.");
        }
        Optional<Seat> existingSeat = seatRepository.findByMusicalIdAndRowAndColumn(mid, row, column);
        if (existingSeat.isPresent()) {
            throw new IllegalStateException("이미 해당 좌석을 예약하셨습니다.");
        }

        Musical musical = performanceRepository.findById(mid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 뮤지컬입니다."));

        // 새로운 좌석 생성 및 저장
        Seat newSeat = new Seat();
        newSeat.setMusical(musical);
        newSeat.setRow(row);
        newSeat.setColumn(column);
        seatRepository.save(newSeat);

        // 예약 생성
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setMusical(musical);
        reservation.setSeat(newSeat);
        reservationRepository.save(reservation);
        return reservationRepository.save(reservation);
    }
}

