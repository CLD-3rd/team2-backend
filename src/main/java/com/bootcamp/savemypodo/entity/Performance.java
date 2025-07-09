package com.bootcamp.savemypodo.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "performances")
public class Performance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    private String title;

    private String summary;

    private LocalDateTime date;

    private int price;

    private int seatNumber;

    // 좌석 정보: 공연 기준 1:N
    @OneToMany(mappedBy = "performance")
    private List<Seat> seats;

    @OneToMany(mappedBy = "performance")
    private List<Reservation> reservations;
}
