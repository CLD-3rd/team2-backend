package com.bootcamp.savemypodo.dto.reservation;

import com.bootcamp.savemypodo.entity.Reservation;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MyReservationResponse(
        Long rid,
        String performanceTitle,
        LocalDateTime date,
        String sid
) {
    public static MyReservationResponse fromEntity(Reservation reservation) {
        return MyReservationResponse.builder()
                .rid(reservation.getId())
                .performanceTitle(reservation.getPerformance().getTitle())
                .date(reservation.getPerformance().getDate())
                .sid(reservation.getSeat().getSid())
                .build();
    }
}
