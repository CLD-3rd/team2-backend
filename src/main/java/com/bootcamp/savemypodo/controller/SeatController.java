package com.bootcamp.savemypodo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bootcamp.savemypodo.dto.seat.SeatDto;
import com.bootcamp.savemypodo.service.SeatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/performances")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/{pid}/seats")
    public ResponseEntity<List<SeatDto>> getSeats(@PathVariable("pid") Long pid) {
        List<SeatDto> seats = seatService.getSeatsByPerformance(pid);
        return ResponseEntity.ok(seats);
    }
}