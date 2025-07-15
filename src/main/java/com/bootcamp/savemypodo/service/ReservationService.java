package com.bootcamp.savemypodo.service;

import com.bootcamp.savemypodo.dto.reservation.MyReservationResponse;
import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.Reservation;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.entity.User;
import com.bootcamp.savemypodo.global.exception.ErrorCode;
import com.bootcamp.savemypodo.global.exception.MusicalException;
import com.bootcamp.savemypodo.global.exception.ReservationException;
import com.bootcamp.savemypodo.repository.MusicalRepository;
import com.bootcamp.savemypodo.repository.ReservationRepository;
import com.bootcamp.savemypodo.repository.SeatRepository;
import com.bootcamp.savemypodo.service.redis.RedisMusicalService;
import com.bootcamp.savemypodo.service.redis.RedisSeatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

	private final MusicalRepository musicalRepository;
	private final ReservationRepository reservationRepository;
	private final SeatRepository seatRepository;
	private final RedisMusicalService redisMusicalService;
	private final RedisSeatService redisSeatService;
	private final RedissonClient redissonClient;

	public void createReservationWithLock(User user, Long mid, String seatName) {
	    String lockKey = "lock:seat:" + mid + ":" + seatName;
	    RLock lock = redissonClient.getLock(lockKey);
	    boolean available=false;

	    try {
	        available = lock.tryLock(5, 10, TimeUnit.SECONDS);
	        if (!available) {
	            throw new ReservationException(ErrorCode.SEAT_LOCK_FAILED);
	        }

	        //  실제 예약 처리
	        doReservation(user, mid, seatName);

	    } catch (InterruptedException e) {
	        throw new ReservationException(ErrorCode.SEAT_LOCK_FAILED);
	    } finally {
	        if (lock.isHeldByCurrentThread()) {
	            lock.unlock();
	        }
	    }
	}

	@Transactional
	public void doReservation(User user, Long mid, String seatName) {
	    boolean user_reserved = reservationRepository.existsByUser_IdAndMusical_Id(user.getId(), mid);
	    if (user_reserved) {
	        throw new ReservationException(ErrorCode.ALREADY_RESERVED_MUSICAL);
	    }

	    Optional<Seat> existingSeat = seatRepository.findByMusicalIdAndSeatName(mid, seatName);
	    if (existingSeat.isPresent()) {
	        throw new ReservationException(ErrorCode.SEAT_ALREADY_RESERVED);
	    }

	    Musical musical = musicalRepository.findById(mid)
	            .orElseThrow(() -> new MusicalException(ErrorCode.MUSICAL_NOT_FOUND));

	    Seat seat = Seat.builder().musical(musical).seatName(seatName).build();
	    seatRepository.save(seat);

	    Reservation reservation = Reservation.builder().user(user).musical(musical).seat(seat).build();
	    reservationRepository.save(reservation);

	    musical.setReservedCount(musical.getReservedCount() + 1);
	    musicalRepository.save(musical);

	    // 캐시 업데이트
	    redisMusicalService.updateOrRefreshCache(user.getId(), mid, -1, true);
	    redisSeatService.cacheSeatsForMusicalIfHot(mid);
	}


	public List<MyReservationResponse> getMyReservationsByUser(User user) {
		List<Reservation> reservations = reservationRepository.findAllByUser(user);
		return reservations.stream().map(MyReservationResponse::fromEntity).collect(Collectors.toList());
	}

	@Transactional
	public void cancelReservation(Long userId, Long musicalId) {
		// 1. 먼저 예약이 실제로 존재하는지 확인
		Optional<Reservation> reservationOpt = reservationRepository.findByUser_IdAndMusical_Id(userId, musicalId);

		if (reservationOpt.isEmpty()) {
			throw new NoSuchElementException("해당 예매 내역이 존재하지 않습니다.");
		}

		// 2. 예매 삭제
		reservationRepository.deleteByUser_IdAndMusical_Id(userId, musicalId);

		// 3. reservedCount(예약자 수) 감소
		Musical musical = musicalRepository.findById(musicalId)
				.orElseThrow(() -> new NoSuchElementException("해당 뮤지컬이 존재하지 않습니다."));

		int updatedCount = Math.max(0, (int) (musical.getReservedCount() - 1)); // 음수 방지
		musical.setReservedCount((long) updatedCount);
		musicalRepository.save(musical);

		// 캐시 업데이트: remainingSeats+, isReserved=false
		redisMusicalService.updateOrRefreshCache(userId, musicalId, +1, true);
		// 좌석 삭제되었으니 재캐싱
		redisSeatService.cacheSeatsForMusicalIfHot(musicalId);

	}

}
