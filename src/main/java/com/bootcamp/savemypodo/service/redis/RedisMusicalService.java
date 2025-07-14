package com.bootcamp.savemypodo.service.redis;

import com.bootcamp.savemypodo.dto.musical.RedisMusicalResponse;
import com.bootcamp.savemypodo.repository.ReservationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMusicalService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String BASE_KEY = "popular:musicals";
    private static final Duration TTL = Duration.ofMinutes(10);

    private final @Qualifier("objectRedisTemplate") RedisTemplate<String, Object> redis;
    private final ReservationRepository reservationRepository;

    @SuppressWarnings("unchecked")
    public void updateOrRefreshCache(Long userId, Long musicalId, Integer deltaRemaining, Boolean updateRemaining) {
        for (String suffix : List.of("new", "hot")) {
            String key = BASE_KEY + ":" + suffix;
            List<?> rawList = (List<?>) redisTemplate.opsForValue().get(key);
            if (rawList == null) continue;

            List<RedisMusicalResponse> list = rawList.stream()
                    .map(item -> objectMapper.convertValue(item, RedisMusicalResponse.class))
                    .collect(Collectors.toList());

            List<RedisMusicalResponse> updated;

            // 1. 로그아웃 상태 이면 모든 isReserved를 false로
            if (userId == null) {
                updated = list.stream()
                        .map(mr -> mr.refreshReserved(false))
                        .collect(Collectors.toList());
                log.info("[Cache] {}: 모든 isReserved를 false로 변경", key);
            } else {
                // 2. 기존 방식대로 isReserved 또는 잔여좌석 갱신
                updated = list.stream()
                        .map(mr -> {
                            boolean nowReserved = reservationRepository.existsByUser_IdAndMusical_Id(userId, mr.id());
                            if (Boolean.TRUE.equals(updateRemaining) && musicalId != null && mr.id().equals(musicalId)) {
                                // 좌석수와 isReserved 동시 갱신
                                return mr.updateEntry(deltaRemaining, nowReserved);
                            } else {
                                // isReserved만 갱신
                                return mr.refreshReserved(nowReserved);
                            }
                        })
                        .collect(Collectors.toList());
                log.info("[Cache] {} isReserved refreshed for user={}", key, userId);
            }

            redisTemplate.opsForValue().set(key, updated, TTL);

            if (Boolean.TRUE.equals(updateRemaining) && userId != null) {
                log.info("[Cache] {} updated: musical={}, Δrem={}, userId={}", key, musicalId, deltaRemaining, userId);
            }
        }
    }
}
