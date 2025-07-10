package com.bootcamp.savemypodo.service;

import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.repository.MusicalRepository;
import com.bootcamp.savemypodo.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final MusicalRepository musicalRepository;
    private final SeatRepository seatRepository;

    public List<Seat> getSeatsByMusicalId(Long musicalId) {
        Musical musical = musicalRepository.findById(musicalId)
                .orElseThrow(() -> new IllegalArgumentException("공연이 존재하지 않습니다."));

        return seatRepository.findByMusical_Id(musicalId);
    }
}
