package com.bootcamp.savemypodo.repository;

import com.bootcamp.savemypodo.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    //List<Seat> findByPerformance_Pid(Long pid);
    //Optional<Seat> findByPerformance_PidAndSid(Long pid, String sid);
}