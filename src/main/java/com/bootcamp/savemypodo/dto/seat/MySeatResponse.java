package com.bootcamp.savemypodo.dto.seat;

import com.bootcamp.savemypodo.entity.Seat;

import lombok.Builder;

@Builder
public record MySeatResponse(
        Long id,
        String seatName
) {
    public static MySeatResponse fromEntity(Seat seat) {
        return MySeatResponse.builder()
                .id(seat.getId())
                .seatName(seat.getSeatName())
                .build();
    }
}