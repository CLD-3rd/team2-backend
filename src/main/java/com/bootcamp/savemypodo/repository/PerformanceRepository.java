package com.bootcamp.savemypodo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bootcamp.savemypodo.entity.Performance;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
	// table 이름과 entity에서 정의한 변수 이름 확인 -> 
	
	// 공연 테이블에서 공연을 최신순으로 정렬
	@Query("SELECT p FROM Performance p ORDER BY p.date DESC")
	List<Performance> findAllByLatest();
	
	// 예매 순 정렬
	@Query("""
			SELECT p FROM Performance p 
			LEFT JOIN Reservation r ON r.performance = p
			GROUP BY p
			ORDER BY COUNT(r.id) DESC
			""")
	List<Performance> findAllByPopular();
	
	// 내가 예매한 공연, performance는 reservation entity에 있는 pid 변수 이름
	@Query("SELECT r.performance FROM Reservation r WHERE r.user.id = :userId")
	List<Performance> findAllByUserId(@Param("userId") Long userId);
}
