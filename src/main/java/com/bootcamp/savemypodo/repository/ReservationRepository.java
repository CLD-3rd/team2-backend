package com.bootcamp.savemypodo.repository;

import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.Reservation;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.entity.User;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	// 유저의 예매내역이 존재하는지 확인
	Optional<Reservation> findByUser_IdAndMusical_Id(Long userId, Long musicalId);

    // 예매 취소
	void deleteByUser_IdAndMusical_Id(Long userId, Long musicalId);

	boolean existsByUserAndMusicalAndSeat(User user, Musical musical, Seat seat);

    // 해당 유저가 예매한 내역 반환
    List<Reservation> findAllByUser(User user);

}


