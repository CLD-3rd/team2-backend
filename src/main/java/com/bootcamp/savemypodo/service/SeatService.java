package com.bootcamp.savemypodo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bootcamp.savemypodo.dto.seat.SeatDto;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public List<SeatDto> getSeatsByPerformance(Long pid) {
        List<Seat> seats = seatRepository.findByPerformance_Pid(pid);
        return seats.stream()
                .map(seat -> new SeatDto(seat.getSid(), seat.getSeatStatus()))
                .toList();
    }
}