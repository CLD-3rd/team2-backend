package com.bootcamp.savemypodo.dto.performance;

import java.time.LocalDateTime;
import com.bootcamp.savemypodo.entity.Musical;

public record PerformanceResponseDto(
        Long pid,
        String title,
        String summary,
        LocalDateTime date,
        int price,
        int seatNumber,
        int reservedSeats
) {
    public static PerformanceResponseDto from(Performance performance, int reservedSeats) {
        return new PerformanceResponseDto(
                performance.getPid(),
                performance.getTitle(),
                performance.getSummary(),
                performance.getDate(),
                performance.getPrice(),
                performance.getSeatNumber(),
                reservedSeats // 여기 주의: reservedSeats는 매개변수에서 받아온 값을 사용해야 합니다.
        );
    }
}


