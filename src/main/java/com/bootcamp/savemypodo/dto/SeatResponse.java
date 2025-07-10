package com.bootcamp.savemypodo.dto;

import com.bootcamp.savemypodo.entity.Seat;
import lombok.Builder;

@Builder
public record SeatResponse(
        Long id,
        Character row,
        Integer column
) {
    public static SeatResponse fromEntity(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .column(seat.getColumn())
                .build();
    }
}
