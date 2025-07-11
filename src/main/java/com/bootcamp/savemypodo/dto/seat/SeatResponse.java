package com.bootcamp.savemypodo.dto.seat;

import java.util.List;

import com.bootcamp.savemypodo.entity.Seat;
import lombok.Builder;

@Builder
public record SeatResponse(
        Long musicalId,
        List<String> reservedSeats
) {
    public static SeatResponse of(Long musicalId, List<String> seatNames) {
        return SeatResponse.builder()
                .musicalId(musicalId)
                .reservedSeats(seatNames)
                .build();
    }
}