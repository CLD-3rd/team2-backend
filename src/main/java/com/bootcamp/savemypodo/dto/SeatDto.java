package com.bootcamp.savemypodo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatDto {
    private Long id;
    private Character row;
    private Integer column;
}