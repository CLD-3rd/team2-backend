package com.bootcamp.savemypodo.dto.performance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.bootcamp.savemypodo.entity.Musical;
import com.fasterxml.jackson.annotation.JsonProperty;

public record MusicalResponseDto(
        Long mid,
        String title,
        String posterUrl,
        String description,
        LocalDate date,
        String timerange,
        Long price,
        String location,
        Long duration,
        Long reservedCount,
        int totalSeats,
        boolean soldOut,
        boolean isReserved         
        
) {
    public static MusicalResponseDto from(Musical musical, boolean isReserved) {
    	int totalSeats = Musical.TOTAL_SEATS;
        String timerange = musical.getStartTime() + " ~ " + musical.getEndTime(); 
    	
    	return new MusicalResponseDto(
        		musical.getMid(),
        		musical.getTitle(),
        		musical.getPosterUrl(),
                musical.getDescription(),
                musical.getDate(),
                timerange,
                musical.getPrice(),
                musical.getLocation(),
                musical.getDuration(),
                musical.getReservedCount(),
                totalSeats, // 전체 좌석 수
                musical.getReservedCount() >= totalSeats, // 매진 여부인데 이건 프론트에서 매진일때 어떻게 처리하는지 보고 수정
                isReserved //사용자가 이 공연을 예약 했는지
                
        );
    }
    
}

