package com.bootcamp.savemypodo.service;

import com.bootcamp.savemypodo.dto.SeatResponse;
import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.repository.PerformanceRepository;
import com.bootcamp.savemypodo.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final PerformanceRepository musicalRepository;
    private final SeatRepository seatRepository;

    public List<Seat> getSeatsByMusicalId(Long musicalId) {
        Musical musical = musicalRepository.findById(musicalId)
                .orElseThrow(() -> new IllegalArgumentException("공연이 존재하지 않습니다."));

        return seatRepository.findByMusical_Id(musicalId);
    }
}
