package com.bootcamp.savemypodo.dto.musical;

import com.bootcamp.savemypodo.entity.Musical;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Builder
public record MusicalResponse(
        Long id,
        String title,
        String posterUrl,
        String description,
        LocalDate date,
        String timeRange,
        Long price,
        String location,
        Long duration,
        Long reservedCount,
        Long remainingSeats
) {
    public static MusicalResponse fromEntity(Musical musical) {
    	Long reservedCount = musical.getReservedCount();
        Long totalSeats = (long) Musical.TOTAL_SEATS;
        Long remainingSeats = totalSeats - reservedCount;
    	
    	return MusicalResponse.builder()
                .id(musical.getId())
                .title(musical.getTitle())
                .posterUrl(musical.getPosterUrl())
                .description(musical.getDescription())
                .date(musical.getDate())
                .timeRange(getTimeRange(musical.getStartTime(), musical.getEndTime()))
                .price(musical.getPrice())
                .location(musical.getLocation())
                .duration(musical.getDuration())
                .reservedCount(musical.getReservedCount())
                .remainingSeats(remainingSeats)
                .build();
    }

    private static String getTimeRange(LocalTime startTime,  LocalTime endTime) {
        // 원하는 시간 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        // 형식에 맞게 문자열 생성
        return startTime.format(formatter) + " ~ " + startTime.format(formatter);
    }
}
