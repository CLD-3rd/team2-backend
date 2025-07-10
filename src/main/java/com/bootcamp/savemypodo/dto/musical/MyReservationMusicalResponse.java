package com.bootcamp.savemypodo.dto.musical;

import com.bootcamp.savemypodo.entity.Musical;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Builder
public record MyReservationMusicalResponse(
        Long id,
        String title,
        String posterUrl,
        LocalDate date,
        String timeRange
) {
    public static MyReservationMusicalResponse fromEntity(Musical musical) {
        return MyReservationMusicalResponse.builder()
                .id(musical.getId())
                .title(musical.getTitle())
                .posterUrl(musical.getPosterUrl())
                .date(musical.getDate())
                .timeRange(getTimeRange(musical.getStartTime(), musical.getEndTime()))
                .build();
    }

    private static String getTimeRange(LocalTime startTime,  LocalTime endTime) {
        // 원하는 시간 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        // 형식에 맞게 문자열 생성
        return startTime.format(formatter) + " ~ " + startTime.format(formatter);
    }
}
