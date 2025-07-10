package com.bootcamp.savemypodo.repository;

import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.Reservation;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	boolean existsByUserAndMusicalAndSeat(User user, Musical musical, Seat seat);

}

