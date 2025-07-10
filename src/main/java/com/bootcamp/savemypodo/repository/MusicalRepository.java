package com.bootcamp.savemypodo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bootcamp.savemypodo.entity.Musical;

@Repository
public interface MusicalRepository extends JpaRepository<Musical, Long> {
	
	// 공연 테이블에서 공연을 최신순으로 정렬
	@Query("SELECT m FROM Musical m ORDER BY m.date DESC")
	List<Musical> findAllByLatest();
	
	// 예매 순 정렬
	List<Musical> findAllByOrderByReservedCountDesc();
	
	// 내가 예매한 공연, 
	@Query("SELECT r.musical FROM Reservation r WHERE r.user.id = :userId")
	List<Musical> findAllByUserId(@Param("userId") Long userId);
}
