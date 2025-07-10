package com.bootcamp.savemypodo.dto.reservation;

public record ReservationResponse(
        boolean reserved,
        boolean soldOut,
        int seatNumber,
        int currentReserved
) {
}


