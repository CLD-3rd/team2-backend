package com.bootcamp.savemypodo.service;

import com.bootcamp.savemypodo.dto.reservation.MyReservationResponse;
import com.bootcamp.savemypodo.entity.Reservation;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.repository.ReservationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<MyReservationResponse> getMyReservationsByUser(User user) {
        List<Reservation> reservations = reservationRepository.findAllByUser(user);
        return reservations.stream()
                .map(MyReservationResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void cancelReservation(Long userId, Long musicalId) {
        reservationRepository.deleteByUser_IdAndMusical_Id(userId, musicalId);
    }
}
