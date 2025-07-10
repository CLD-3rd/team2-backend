package com.bootcamp.savemypodo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;

import com.bootcamp.savemypodo.dto.performance.MusicalResponseDto;
import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.PerformanceSortType;
import com.bootcamp.savemypodo.repository.PerformanceRepository;
import com.bootcamp.savemypodo.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceService {
	private final PerformanceRepository performanceRepository;
	private final ReservationRepository reservationRepository;
	
	public List<MusicalResponseDto> getPerformances(PerformanceSortType sort, Long userId){
		List<Musical> musicals;

	    if (sort == PerformanceSortType.MOST_RESERVED) {
	        musicals = performanceRepository.findAllByOrderByReservedCountDesc();
	    } else if (sort == PerformanceSortType.MINE && userId != null) {
	        musicals = performanceRepository.findAllByUserId(userId);
	    } else {
	        musicals = performanceRepository.findAllByLatest();
	    }
	    
	    // 사용자가 이 공연을 예매했는지
	    return musicals.stream()
	            .map(musical -> {
	                boolean isReserved = (userId != null)
	                        && reservationRepository.existsByUser_IdAndMusical_Mid(userId, musical.getMid());

	                return MusicalResponseDto.from(musical, isReserved);
	            })
	            .collect(Collectors.toList());
	}
}

