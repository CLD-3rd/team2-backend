package com.bootcamp.savemypodo.dto.reservation;

import com.bootcamp.savemypodo.dto.musical.MusicalResponse;
import com.bootcamp.savemypodo.dto.musical.MyReservationMusicalResponse;
import com.bootcamp.savemypodo.dto.seat.SeatResponse;
import com.bootcamp.savemypodo.entity.Reservation;
import lombok.Builder;

@Builder
public record MyReservationResponse(
        Long id,
        MyReservationMusicalResponse musical,
        SeatResponse seat,
        Long price
) {
    public static MyReservationResponse fromEntity(Reservation reservation) {
        return MyReservationResponse.builder()
                .id(reservation.getId())
                .musical(MyReservationMusicalResponse.fromEntity(reservation.getMusical()))
                .seat(SeatResponse.fromEntity(reservation.getSeat()))
                .price(reservation.getMusical().getPrice())
                .build();
    }
}
