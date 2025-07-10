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
        Long reservedCount
) {
    public static MusicalResponse fromEntity(Musical musical) {
        return MusicalResponse.builder()
                .id(musical.getId())
                .title(musical.getTitle())
                .posterUrl(musical.getPosterUrl())
                .date(musical.getDate())
                .timeRange(getTimeRange(musical.getStartTime(), musical.getEndTime()))
                .price(musical.getPrice())
                .location(musical.getLocation())
                .duration(musical.getDuration())
                .reservedCount(musical.getReservedCount())
                .build();
    }

    private static String getTimeRange(LocalTime startTime,  LocalTime endTime) {
        // 원하는 시간 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        // 형식에 맞게 문자열 생성
        return startTime.format(formatter) + " ~ " + startTime.format(formatter);
    }
}
