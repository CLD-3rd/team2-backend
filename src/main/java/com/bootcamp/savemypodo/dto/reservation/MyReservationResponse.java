package com.bootcamp.savemypodo.dto.reservation;

import com.bootcamp.savemypodo.dto.musical.MusicalResponse;
import com.bootcamp.savemypodo.dto.musical.MyReservationMusicalResponse;
import com.bootcamp.savemypodo.dto.seat.MySeatResponse;
import com.bootcamp.savemypodo.entity.Reservation;
import lombok.Builder;

@Builder
public record MyReservationResponse(
        Long id,
        MyReservationMusicalResponse musical,
        MySeatResponse seat,
        Long price
) {
    public static MyReservationResponse fromEntity(Reservation reservation) {
        return MyReservationResponse.builder()
                .id(reservation.getId())
                .musical(MyReservationMusicalResponse.fromEntity(reservation.getMusical()))
                .seat(MySeatResponse.fromEntity(reservation.getSeat()))
                .price(reservation.getMusical().getPrice())
                .build();
    }
}
