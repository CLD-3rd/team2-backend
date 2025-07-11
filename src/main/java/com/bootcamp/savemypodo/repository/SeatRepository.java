package com.bootcamp.savemypodo.repository;

import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByMusical_Id(Long musicalId);

    Optional<Seat> findByMusicalIdAndSeatName(Long musicalId, String seatName);

	
}

