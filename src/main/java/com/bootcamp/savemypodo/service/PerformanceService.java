package com.bootcamp.savemypodo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bootcamp.savemypodo.dto.musical.MusicalResponse;
//import com.bootcamp.savemypodo.dto.performance.MusicalResponseDto;
import com.bootcamp.savemypodo.entity.Musical;
//import com.bootcamp.savemypodo.entity.MusicalSortType;
import com.bootcamp.savemypodo.global.enums.SortType;
import com.bootcamp.savemypodo.repository.MusicalRepository;
import com.bootcamp.savemypodo.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceService {
	private final MusicalRepository musicalRepository;
	private final ReservationRepository reservationRepository;
	
	public List<MusicalResponse> getPerformances(SortType sort, Long userId){
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
	    
	    // 사용자가 이 공연을 예매했는지(isReserved)
	    /*return musicals.stream()
	            .map(musical -> {
	                boolean isReserved = (userId != null)
	                        && reservationRepository.existsByUser_IdAndMusical_Mid(userId, musical.getMid());

	                return MusicalResponse.from(musical, isReserved);
	            })
	            .collect(Collectors.toList());*/
	
}

