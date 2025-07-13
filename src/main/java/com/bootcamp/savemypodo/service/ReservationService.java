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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final MusicalRepository musicalRepository;
	private final ReservationRepository reservationRepository;
	private final SeatRepository seatRepository;
	private final RedisMusicalService redisMusicalService;
	private final RedisSeatService redisSeatService;

	@Transactional
	public void createReservation(User user, Long mid, String seatName) {

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

		// 새로운 좌석 생성 및 저장
		Seat seat = Seat.builder().musical(musical).seatName(seatName).build();
		seatRepository.save(seat);

		// 예약 생성
		Reservation reservation = Reservation.builder().user(user).musical(musical).seat(seat).build();
		reservationRepository.save(reservation);

		// 공연의 reservedCount 증가
		musical.setReservedCount(musical.getReservedCount() + 1);
		musicalRepository.save(musical);

		// 캐시 업데이트: remainingSeats–, isReserved=true
		redisMusicalService.updateOrRefreshCache(user.getId(), mid, -1, true);
		// 좌석 추가되었으니 재캐싱
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
//        redisSeatService.cacheSeatsForMusical(musicalId);  // 좌석 삭제되었으니 재캐싱

	}
}
