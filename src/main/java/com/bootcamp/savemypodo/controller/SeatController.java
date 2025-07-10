package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/musicals")
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/{musicalId}/seats")
    public ResponseEntity<List<SeatResponse>> getSeats(@PathVariable("musicalId") Long musicalId) {
        List<Seat> seats = seatService.getSeatsByMusicalId(musicalId);

        List<SeatResponse> response = seats.stream()
                .map(SeatResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}