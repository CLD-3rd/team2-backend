package com.bootcamp.savemypodo.dto.musical;

import java.time.LocalDate;

public record RedisMusicalResponse(
        Long id,
        String title,
        String timeRange,
        String description,
        Long remainingSeats,
        Long totalSeats,
        Long price,
        String posterUrl,
        boolean isReserved,
        LocalDate date,
        String location,
        Long duration
) {

    /**
     * remainingSeats 와 isReserved 만 바꿔서 새로운 DTO 생성
     */
    public RedisMusicalResponse updateEntry(int deltaRemaining, boolean newReserved) {
        return new RedisMusicalResponse(
                id,
                title,
                timeRange,
                description,
                remainingSeats + deltaRemaining,
                totalSeats,
                price,
                posterUrl,
                newReserved,
                date,
                location,
                duration
        );
    }

    /**
     * isReserved 플래그만 새로 설정하여 반환
     */
    public RedisMusicalResponse refreshReserved(boolean newReserved) {
        return new RedisMusicalResponse(
                id,
                title,
                timeRange,
                description,
                remainingSeats,
                totalSeats,
                price,
                posterUrl,
                newReserved,
                date,
                location,
                duration
        );
    }
}