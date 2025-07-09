package com.bootcamp.savemypodo.dto.reservation;

public record ReservationResponseDto(
 boolean reserved,
 boolean soldOut,
 int seatNumber,
 int currentReserved
) {}


