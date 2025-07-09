package com.bootcamp.savemypodo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bootcamp.savemypodo.dto.performance.PerformanceResponseDto;
import com.bootcamp.savemypodo.entity.Performance;
import com.bootcamp.savemypodo.entity.PerformanceSortType;
import com.bootcamp.savemypodo.repository.PerformanceRepository;
import com.bootcamp.savemypodo.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceService {
	private final PerformanceRepository performanceRepository;
	private final ReservationRepository reservationRepository;
	
	public List<PerformanceResponseDto> getPerformances(PerformanceSortType sortType, Long userId){
		List<Performance> performances;
        switch (sortType) {
            case POPULAR -> performances = performanceRepository.findAllByPopular();
            case MINE -> performances = performanceRepository.findAllByUserId(userId);
            default -> performances = performanceRepository.findAllByLatest();
        }

        return performances.stream() // 현재 신청 인원 반환
                .map(p -> {
                	// reservation table에서 공연 id 개수를 세어 저장
                    int reserved = reservationRepository.countByPerformance_Pid(p.getPid());
                    return PerformanceResponseDto.from(p, reserved);
                })
                .collect(Collectors.toList());
	}
}
