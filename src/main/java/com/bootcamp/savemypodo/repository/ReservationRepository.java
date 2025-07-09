package com.bootcamp.savemypodo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bootcamp.savemypodo.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
