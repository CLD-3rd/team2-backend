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

	// 공연 ID로 예약된 row 개수 반환
    int countByMusical_Mid(Long mid);
    
    // 해당 유저가 해당 공연을 예매했는지
    boolean existsByUser_IdAndMusical_Mid(Long id, Long mid);  
    
    // 예매 취소
    @Modifying
    @Transactional
    @Query("DELETE FROM Reservation r WHERE r.user.id = :id AND r.musical.id = :mid")
    void deleteByUser_IdAndMusical_Mid(@Param("id") Long id, @Param("mid") Long mid);
}