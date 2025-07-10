package com.bootcamp.savemypodo.service;

import com.bootcamp.savemypodo.dto.musical.MusicalResponse;
import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.global.enums.SortType;
import com.bootcamp.savemypodo.repository.MusicalRepository;
import com.bootcamp.savemypodo.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicalService {
    private final MusicalRepository musicalRepository;
    private final ReservationRepository reservationRepository;

    public List<MusicalResponse> getPerformances(SortType sort, Long userId) {
        List<Musical> musicals;

        switch (sort) {
            case MOST_RESERVED:
                musicals = musicalRepository.findAllByOrderByReservedCountDesc();
                break;
            case MINE:
                if (userId != null) {
                    musicals = musicalRepository.findAllByUserId(userId);
                } else {
                    musicals = List.of();
                }
                break;
            case LATEST:
            default:
                musicals = musicalRepository.findAllByLatest();
                break;
        }


        return musicals.stream()
                .map(MusicalResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

