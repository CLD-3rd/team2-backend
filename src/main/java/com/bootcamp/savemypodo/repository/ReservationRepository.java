package com.bootcamp.savemypodo.repository;

import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.Reservation;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	boolean existsByUserAndMusicalAndSeat(User user, Musical musical, Seat seat);
// 공연 ID로 예약된 row 개수 반환
    int countByPerformance_Pid(Long pid);
// 해당 유저가 해당 공연을 예매했는지
    boolean existsByUser_IdAndPerformance_Pid(Long uid, Long pid);
// 예매 취소
    void deleteByUser_IdAndPerformance_Pid(@Param("uid") Long uid, @Param("pid") Long pid);

    // 해당 유저가 예매한 내역 반환
    List<Reservation> findAllByUser(User user);

}

