package com.bootcamp.savemypodo.service;

import com.bootcamp.savemypodo.dto.musical.MusicalResponse;
import com.bootcamp.savemypodo.dto.musical.RedisMusicalResponse;
import com.bootcamp.savemypodo.entity.Musical;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.global.enums.SortType;
import com.bootcamp.savemypodo.repository.MusicalRepository;
import com.bootcamp.savemypodo.repository.ReservationRepository;
import com.bootcamp.savemypodo.repository.SeatRepository;
import com.bootcamp.savemypodo.service.redis.RedisMusicalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final @Qualifier("objectRedisTemplate") RedisTemplate<String, Object> redis;
    private static volatile boolean cacheAvailable = true;


    public List<MusicalResponse> getPerformances(SortType sort, Long userId) {

        // 1) MINE 처리 (캐시 없이 전체 조회)
        if (sort == SortType.MINE) {
            List<Musical> mine = (userId != null)
                    ? musicalRepository.findAllByUserId(userId)
                    : List.of();
            return toResponses(mine, userId);
        }

        // 2) 최신순 캐시 키
        String latestKey = BASE_KEY + ":new";
        String hotKey = BASE_KEY + ":hot";

        boolean cacheAvailable = true;
        List<MusicalResponse> top5 = null;

        // 3) 최신순 캐시 조회
        try {
            Object raw = redis.opsForValue().get(latestKey);
            if (raw != null) {
                log.info("✅ Cache hit for key {}", latestKey);

                @SuppressWarnings("unchecked")
                List<?> rawList = (List<?>) raw;
                if (!rawList.isEmpty()) {
                    Object first = rawList.get(0);
                    if (first instanceof MusicalResponse) {
                        @SuppressWarnings("unchecked")
                        List<MusicalResponse> casted = (List<MusicalResponse>) rawList;
                        top5 = casted;
                    } else {
                        top5 = rawList.stream()
                                .map(item -> objectMapper.convertValue(item, MusicalResponse.class))
                                .collect(Collectors.toList());
                    }
                }

                // 로그인한 경우 isReserved만 갱신
                if (userId != null && top5 != null) {
                    top5 = top5.stream()
                            .map(mr -> {
                                boolean nowReserved = reservationRepository
                                        .existsByUser_IdAndMusical_Id(userId, mr.id());
                                return mr.withIsReserved(nowReserved);
                            })
                            .collect(Collectors.toList());
                }
            } else {
                log.info("❌ Cache miss for key {}", latestKey);
            }
        } catch (Exception e) {
            log.warn("Redis 접근 불가 – 캐시 사용 안 함: {}", e.toString());
            cacheAvailable = false;
        }


        // 4) Redis 장애이거나 캐시 미스 시 DB에서 조회 & 캐시 저장
        if (!cacheAvailable || top5 == null) {
            log.info("Loading Top5 from DB for key {} (cacheAvailable={})", latestKey, cacheAvailable);
            List<Musical> rawTop5 = musicalRepository.findTop5ByOrderByDateDesc();
            top5 = toResponses(rawTop5, userId);

            if (cacheAvailable) {
                try {
                    redis.opsForValue().set(latestKey, top5, TTL);
                    log.info("💾 Saved {} items to cache key {}", top5.size(), latestKey);
                } catch (Exception e) {
                    log.warn("Redis 재접속 실패 – 캐시 갱신 생략: {}", e.toString());
                }
            }
        }

        // 5) 항상 예매많은순 Top5도 함께 캐싱 (응답은 X)
        if (cacheAvailable) {
            try {
                Object hotRaw = redis.opsForValue().get(hotKey);
                if (hotRaw == null) {
                    log.info("Loading Hot Top5 from DB for key {}", hotKey);
                    List<Musical> hotTop5 = musicalRepository.findTop5ByOrderByReservedCountDesc();
                    List<MusicalResponse> hotTop5Response = toResponses(hotTop5, userId);
                    redis.opsForValue().set(hotKey, hotTop5Response, TTL);
                    log.info("💾 Saved {} items to cache key {}", hotTop5Response.size(), hotKey);
                }
            } catch (Exception e) {
                log.warn("Redis hotTop5 캐시 접근 실패 – 생략: {}", e.toString());
            }
        }

        // 6) 전체 리스트에서 Top5 제외하여 나머지 조회 (최신순 기준)
        List<Musical> allRaw = musicalRepository.findAllByLatest();
        Set<Long> top5Ids = top5.stream()
                .map(MusicalResponse::id)
                .collect(Collectors.toSet());

        List<MusicalResponse> rest = allRaw.stream()
                .filter(m -> !top5Ids.contains(m.getId()))
                .map(m -> {
                    boolean isRes = userId != null &&
                            reservationRepository.existsByUser_IdAndMusical_Id(userId, m.getId());
                    return MusicalResponse.fromEntity(m, isRes);
                })
                .collect(Collectors.toList());

        // 7) 합쳐서 반환
        List<MusicalResponse> combined = new ArrayList<>(top5.size() + rest.size());
        combined.addAll(top5);
        combined.addAll(rest);
        return combined;
    }

    private List<MusicalResponse> toResponses(List<Musical> list, Long userId) {
        return list.stream().map(m -> {
            boolean isReserved = userId != null
                    && reservationRepository.existsByUser_IdAndMusical_Id(userId, m.getId());
            return MusicalResponse.fromEntity(m, isReserved);
        }).collect(Collectors.toList());
    }

    public List<Seat> getSeatsByMusicalId(Long musicalId) {
        String redisKey = "seats:hot:" + musicalId;
        String hotKey = "popular:musicals:hot";

        Object hotRaw = redis.opsForValue().get(hotKey);
        boolean isHot = false;

        if (hotRaw instanceof List<?> hotList) {
            isHot = hotList.stream().anyMatch(item -> {
                Long id = objectMapper.convertValue(item, RedisMusicalResponse.class).id();
                return id.equals(musicalId);
            });
        }

        // 인기 공연이고 캐시가 있다면 캐시 사용
        if (isHot) {
            Object cachedRaw = redis.opsForValue().get(redisKey);
            if (cachedRaw instanceof List<?> cached && !cached.isEmpty() && cached.get(0) instanceof String) {
                log.info("✅ 인기 공연 캐시 사용: {}", redisKey);
                return cached.stream().map(name -> Seat.builder().seatName((String) name).build())
                        .collect(Collectors.toList());
            }

            // 인기 공연이지만 캐시가 없다면 → DB 조회 후 캐시 저장
            log.info("❌ 인기 공연이지만 캐시 없음 → DB 조회 후 캐시 저장");
            List<Seat> fromDb = seatRepository.findByMusical_Id(musicalId);

            List<String> seatNames = fromDb.stream().map(Seat::getSeatName).toList();

            redis.opsForValue().set(redisKey, seatNames, Duration.ofMinutes(10));
            return fromDb;
        }

        // 일반 공연 → DB 조회
        log.info("📦 일반 공연 → DB 조회: musicalId={}", musicalId);
        return seatRepository.findByMusical_Id(musicalId);
    }
}
