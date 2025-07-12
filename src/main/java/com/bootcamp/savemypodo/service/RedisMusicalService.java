package com.bootcamp.savemypodo.service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bootcamp.savemypodo.dto.musical.RedisMusicalResponse;
import com.bootcamp.savemypodo.repository.ReservationRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMusicalService {
	@Autowired
	private ObjectMapper objectMapper;
	
	private static final String BASE_KEY = "popular:musicals";
    private static final Duration TTL = Duration.ofMinutes(10);

    private final @Qualifier("objectRedisTemplate") RedisTemplate<String, Object> redis;
    private final ReservationRepository reservationRepository;
    /*
    @SuppressWarnings("unchecked")
    public void updateMusicalEntry(Long musicalId, int deltaRemaining, boolean isReserved, Long userId) {
        for (String suffix : List.of("new", "hot")) {
            String key = BASE_KEY + ":" + suffix;
            List<?> rawList = (List<?>) redis.opsForValue().get(key);
            if (rawList == null) continue;

            List<RedisMusicalResponse> list = rawList.stream()
                .map(item -> objectMapper.convertValue(item, RedisMusicalResponse.class))
                .collect(Collectors.toList());
            
            List<RedisMusicalResponse> updated = list.stream()
                    .map((RedisMusicalResponse mr) -> {                        // ← 람다 파라미터에 타입 명시
                        boolean nowReserved = userId != null
                            && reservationRepository.existsByUser_IdAndMusical_Id(userId, mr.getId());
                        return mr.updateEntry(deltaRemaining, nowReserved);
                    })
                    .collect(Collectors.toList());    

            redis.opsForValue().set(key, updated, TTL);
            log.info("[Cache] {} updated: musical={}, Δrem={}, reserved={}",
                     key, musicalId, deltaRemaining, isReserved);
        }
    }*/

    /**
     * 로그인 직후 호출: 캐시에 있는 Top5 리스트의 isReserved 플래그만
     * 현재 userId 기준으로 재설정하여 덮어쓰기
     * @param userId 로그인한 사용자 ID
     */
    /*
    @SuppressWarnings("unchecked")
    public void refreshIsReservedForUser(Long userId) {
    	for (String suffix : List.of("new", "hot")) {
            String key = BASE_KEY + ":" + suffix;
            List<?> rawList = (List<?>) redis.opsForValue().get(key);
            if (rawList == null) continue;

            // objectMapper를 이용해 안전하게 변환
            List<RedisMusicalResponse> list = rawList.stream()
                .map((Object item) -> objectMapper.convertValue(item, RedisMusicalResponse.class))
                .collect(Collectors.toList());

            List<RedisMusicalResponse> updated = list.stream()
                .map(mr -> {
                    boolean nowReserved = userId != null
                        && reservationRepository.existsByUser_IdAndMusical_Id(userId, mr.getId());
                    return mr.refreshReserved(nowReserved);
                })
                .toList();

            redis.opsForValue().set(key, updated, TTL);
            log.info("[Cache] {} isReserved refreshed for user={}", key, userId);
        }
    }*/
    @SuppressWarnings("unchecked")
    public void updateOrRefreshCache(Long userId, Long musicalId, Integer deltaRemaining, Boolean updateRemaining) {
        for (String suffix : List.of("new", "hot")) {
            String key = BASE_KEY + ":" + suffix;
            List<?> rawList = (List<?>) redis.opsForValue().get(key);
            if (rawList == null) continue;

            List<RedisMusicalResponse> list = rawList.stream()
                .map(item -> objectMapper.convertValue(item, RedisMusicalResponse.class))
                .collect(Collectors.toList());

            List<RedisMusicalResponse> updated = list.stream()
                .map(mr -> {
                    boolean nowReserved = userId != null &&
                        reservationRepository.existsByUser_IdAndMusical_Id(userId, mr.getId());

                    if (Boolean.TRUE.equals(updateRemaining) && musicalId != null && mr.getId().equals(musicalId)) {
                        // 좌석수와 isReserved 동시 갱신
                        return mr.updateEntry(deltaRemaining, nowReserved);
                    } else {
                        // isReserved만 갱신
                        return mr.refreshReserved(nowReserved);
                    }
                })
                .collect(Collectors.toList());

            redis.opsForValue().set(key, updated, TTL);
            if (Boolean.TRUE.equals(updateRemaining)) {
                log.info("[Cache] {} updated: musical={}, Δrem={}, userId={}", key, musicalId, deltaRemaining, userId);
            } else {
                log.info("[Cache] {} isReserved refreshed for user={}", key, userId);
            }
        }
    }
}
