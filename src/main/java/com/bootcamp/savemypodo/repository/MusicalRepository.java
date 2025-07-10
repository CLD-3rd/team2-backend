package com.bootcamp.savemypodo.repository;

import com.bootcamp.savemypodo.entity.Musical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MusicalRepository extends JpaRepository<Musical, Long> {
    // table 이름과 entity에서 정의한 변수 이름 확인 ->

    // 공연 테이블에서 공연을 최신순으로 정렬
    @Query("SELECT p FROM Musical p ORDER BY p.date DESC")
    List<Musical> findAllByLatest();

    // 예매 순 정렬
    @Query("""
			SELECT p FROM Musical p 
			LEFT JOIN Reservation r ON r.musical = p
			GROUP BY p
			ORDER BY COUNT(r.id) DESC
			""")
    List<Musical> findAllByPopular();

    // 내가 예매한 공연, musical은 reservation entity에 있는 pid 변수 이름
    @Query("SELECT r.musical FROM Reservation r WHERE r.user.id = :userId")
    List<Musical> findAllByUserId(@Param("userId") Long userId);
}
