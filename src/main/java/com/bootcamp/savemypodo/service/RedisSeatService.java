package com.bootcamp.savemypodo.service;

import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.repository.SeatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSeatService {

    private final SeatRepository seatRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String SEAT_KEY_PREFIX = "seats:hot:";
    private static final String HOT_KEY = "popular:musicals:hot";
    private static final Duration TTL = Duration.ofMinutes(10);

    public boolean isHotMusical(Long musicalId) {
        Object raw = redisTemplate.opsForValue().get(HOT_KEY);
        if (raw instanceof List<?> list) {
            return list.stream().anyMatch(item -> {
                Long id = objectMapper.convertValue(item, com.bootcamp.savemypodo.dto.musical.RedisMusicalResponse.class).getId();
                return id.equals(musicalId);
            });
        }
        return false;
    }

    public List<String> getSeatsWithCachingIfHot(Long musicalId) {
        String key = SEAT_KEY_PREFIX + musicalId;

        if (isHotMusical(musicalId)) {
            Object raw = redisTemplate.opsForValue().get(key);

            if (raw instanceof List<?> cached && !cached.isEmpty() && cached.get(0) instanceof String) {
                log.info("✅ Redis 좌석 캐시 사용: {}", key);
                return cached.stream().map(Object::toString).toList();
            }

            log.info("❌ Redis 좌석 캐시 없음 → DB 조회 + 캐시 저장: {}", musicalId);
            List<String> fromDb = seatRepository.findByMusical_Id(musicalId).stream()
                    .map(Seat::getSeatName)
                    .toList();

            redisTemplate.opsForValue().set(key, fromDb, TTL);
            return fromDb;
        }

        log.info("📦 일반 공연 → DB 조회만 수행");
        return seatRepository.findByMusical_Id(musicalId).stream()
                .map(Seat::getSeatName)
                .toList();
    }
    
    public void cacheSeatsForMusicalIfHot(Long musicalId) {
        if (!isHotMusical(musicalId)) {
            log.info("🔥 {}번 뮤지컬은 인기 공연이 아님 → 캐시 건너뜀", musicalId);
            return;
        }

        String key = SEAT_KEY_PREFIX + musicalId;

        List<String> seatNames = seatRepository.findByMusical_Id(musicalId).stream()
                .map(Seat::getSeatName)
                .toList();

        redisTemplate.opsForValue().set(key, seatNames, TTL);
        log.info("💾 Redis 좌석 캐시 저장 완료: key={}, size={}", key, seatNames.size());
    }

}
