package com.bootcamp.savemypodo.service;

import com.bootcamp.savemypodo.dto.musical.MusicalResponse;
import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.global.enums.SortType;
import com.bootcamp.savemypodo.repository.MusicalRepository;
import com.bootcamp.savemypodo.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
                    //musicals = List.of();
                	throw new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "로그인이 필요합니다."
                    );
                }
                break;
            case LATEST:
            default:
                musicals = musicalRepository.findAllByLatest();
                break;
        }
        
        return musicals.stream()
        	    .map((Musical musical) -> {
        	        boolean isReserved = false;

        	        if (userId != null) {
        	            isReserved = reservationRepository.existsByUser_IdAndMusical_Id(userId, musical.getId());
        	        }

        	        return MusicalResponse.fromEntity(musical, isReserved); 
        	    })
        	    .collect(Collectors.toList());

        /*
        return musicals.stream()
                .map(MusicalResponse::fromEntity)
                .collect(Collectors.toList());*/
    }
}

