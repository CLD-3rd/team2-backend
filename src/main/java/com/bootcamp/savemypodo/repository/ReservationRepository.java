package com.bootcamp.savemypodo.repository;

import com.bootcamp.savemypodo.entity.User;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bootcamp.savemypodo.entity.Reservation;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // 예매 취소
	void deleteByUser_IdAndMusical_Id(Long userId, Long musicalId);


//    // 해당 유저가 해당 공연을 예매했는지
//    boolean existsByUser_IdAndPerformance_Pid(Long uid, Long pid);


    // 해당 유저가 예매한 내역 반환
    List<Reservation> findAllByUser(User user);

}