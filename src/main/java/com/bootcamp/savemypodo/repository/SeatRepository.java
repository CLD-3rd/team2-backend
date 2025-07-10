package com.bootcamp.savemypodo.repository;

import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	 List<Seat> findByMusical_Id(Long musicalId);
	 Optional<Seat> findByMusicalIdAndRowAndColumn(Long musicalId, Character row, Integer column);
   List<Seat> findByPerformance_Pid(Long pid);
   Optional<Seat> findByPerformance_PidAndSid(Long pid, String sid);

}
