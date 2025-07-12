package com.bootcamp.savemypodo.service;

import com.bootcamp.savemypodo.dto.musical.MusicalResponse;
import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.global.enums.SortType;
import com.bootcamp.savemypodo.repository.MusicalRepository;
import com.bootcamp.savemypodo.repository.ReservationRepository;
import com.bootcamp.savemypodo.repository.SeatRepository;
import com.bootcamp.savemypodo.service.RedisMusicalService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;    
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.ArrayList;

@Slf4j 
@Service
@RequiredArgsConstructor
public class MusicalService {
    private final MusicalRepository musicalRepository;
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final RedisMusicalService redisMusicalService;
    
    private static final String BASE_KEY = "popular:musicals";
    private static final Duration TTL = Duration.ofMinutes(10);
    private final ObjectMapper objectMapper;
    
    private final MusicalRepository repo;
    private final @Qualifier("objectRedisTemplate") RedisTemplate<String, Object> redis;
    
    public List<MusicalResponse> getPerformances(SortType sort, Long userId) {
    
    	
    	// 1) MINE 처리 (캐시 없이 전체 조회)
        if (sort == SortType.MINE) {
            List<Musical> mine = (userId != null)
                ? musicalRepository.findAllByUserId(userId)
                : List.of();
            return toResponses(mine, userId);
        }

        // 2) Top5 캐시 키 결정
        String cacheKey = (sort == SortType.MOST_RESERVED)
            ? BASE_KEY + ":hot"
            : BASE_KEY + ":new";

        // 3) 캐시 조회
        Object raw = redis.opsForValue().get(cacheKey);
        List<MusicalResponse> top5 = null;
        if (raw != null) {
            log.info("✅ Cache hit for key {}", cacheKey);

            @SuppressWarnings("unchecked")
            List<?> rawList = (List<?>) raw;            // 명시적 캐스팅
            if (!rawList.isEmpty()) {
                Object first = rawList.get(0);
                if (first instanceof MusicalResponse) {
                    // 이미 DTO 리스트인 경우
                    @SuppressWarnings("unchecked")
                    List<MusicalResponse> casted = (List<MusicalResponse>) rawList;
                    top5 = casted;
                } else {
                    // 구(JSON) 포맷 → LinkedHashMap 리스트인 경우
                    top5 = rawList.stream()
                        .map((Object item) ->
                            objectMapper.convertValue(item, MusicalResponse.class)
                        )
                        .collect(Collectors.toList());
                }
            }
            // 로그인을 하면 redis의 값 변경(isReserved만)
            if (userId != null && top5 != null) {
                redisMusicalService.updateOrRefreshCache(userId, null, null, false);
                
                top5 = top5.stream()
                		.map((MusicalResponse mr) -> {
                	        boolean nowReserved = reservationRepository.existsByUser_IdAndMusical_Id(userId, mr.id());
                	        return mr.withIsReserved(nowReserved);
                	    })
                	    .collect(Collectors.toList());
            }
            
        } else {
            log.info("❌ Cache miss for key {}", cacheKey);
        }

        // 4) 캐시 미스 시 DB에서 Top5 조회 & 캐시 저장
        if (top5 == null) {
            log.info("Loading Top5 from DB for key {}", cacheKey);
            List<Musical> rawTop5 = (sort == SortType.MOST_RESERVED)
                ? musicalRepository.findTop5ByOrderByReservedCountDesc()
                : musicalRepository.findTop5ByOrderByDateDesc();
            top5 = toResponses(rawTop5, userId);
            redis.opsForValue().set(cacheKey, top5, TTL);
            log.info("💾 Saved {} items to cache key {}", top5.size(), cacheKey);
        }

        // 5) 전체 리스트에서 Top5 제외하여 나머지 조회
        List<Musical> allRaw = (sort == SortType.MOST_RESERVED)
            ? musicalRepository.findAllByOrderByReservedCountDesc()
            : musicalRepository.findAllByLatest();

        Set<Long> top5Ids = top5.stream()
            .map(MusicalResponse::id)
            .collect(Collectors.toSet());

        List<MusicalResponse> rest = allRaw.stream()
            .filter(m -> !top5Ids.contains(m.getId()))
            .map(m -> {
                boolean isReserved = userId != null &&
                    reservationRepository.existsByUser_IdAndMusical_Id(userId, m.getId());
                return MusicalResponse.fromEntity(m, isReserved);
            })
            .collect(Collectors.toList());

        // 6) 합쳐서 반환
        List<MusicalResponse> combined = new ArrayList<>(top5.size() + rest.size());
        combined.addAll(top5);
        combined.addAll(rest);
        return combined;
    }

    private List<MusicalResponse> toResponses(List<Musical> list, Long userId) {
        return list.stream()
            .map(m -> {
                boolean isReserved = userId != null &&
                    reservationRepository.existsByUser_IdAndMusical_Id(userId, m.getId());
                return MusicalResponse.fromEntity(m, isReserved);
            })
            .collect(Collectors.toList());
    }
    
    public List<Seat> getSeatsByMusicalId(Long musicalId) {
        Musical musical = musicalRepository.findById(musicalId)
            .orElseThrow(() -> new IllegalArgumentException("공연이 존재하지 않습니다."));

        return seatRepository.findByMusical_Id(musicalId);
    }
}  


