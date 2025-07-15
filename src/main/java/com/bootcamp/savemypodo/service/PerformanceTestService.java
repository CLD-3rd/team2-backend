package com.bootcamp.savemypodo.service;

import com.bootcamp.savemypodo.dto.seat.SeatResponse;
import com.bootcamp.savemypodo.dto.musical.MusicalResponse;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.repository.MusicalRepository;
import com.bootcamp.savemypodo.repository.SeatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Profile("performance")
@Service
@RequiredArgsConstructor
public class PerformanceTestService {

    private final MusicalRepository musicalRepository;
    private final SeatRepository seatRepository;
    private final RedisTemplate<String, Object> redis;
    @Autowired
    private final ObjectMapper objectMapper;

    private static final String HOT_KEY = "performance:hot:top5";
    private static final String SEAT_KEY_PREFIX = "performance:seats:";

    private static final Duration TTL = Duration.ofMinutes(10);

    public List<MusicalResponse> getAllFromDB() { 
        return musicalRepository.findAll().stream()
                .map(m -> MusicalResponse.fromEntity(m, false))
                .collect(Collectors.toList());
    }

    public List<MusicalResponse> getMixedWithCache() {
        Object raw = redis.opsForValue().get(HOT_KEY);
        List<MusicalResponse> top5;
        if (raw == null) {
            top5 = musicalRepository.findTop5ByOrderByReservedCountDesc().stream()
                    .map(m -> MusicalResponse.fromEntity(m, false))
                    .collect(Collectors.toList());
            redis.opsForValue().set(HOT_KEY, top5, TTL);
        } else {
            top5 = ((List<?>) raw).stream()
                    .map(obj -> objectMapper.convertValue(obj, MusicalResponse.class))
                    .collect(Collectors.toList());
        }

        List<MusicalResponse> rest = musicalRepository.findAll().stream()
                .filter(m -> top5.stream().noneMatch(t -> t.id().equals(m.getId())))
                .map(m -> MusicalResponse.fromEntity(m, false))
                .collect(Collectors.toList());

        top5.addAll(rest);
        return top5;
    }

    public List<MusicalResponse> getOnlyTop5FromCache() {
        Object raw = redis.opsForValue().get(HOT_KEY);
        if (raw != null) {
            return ((List<?>) raw).stream()
                    .map(obj -> objectMapper.convertValue(obj, MusicalResponse.class))
                    .collect(Collectors.toList());
        } else {
            List<MusicalResponse> top5 = musicalRepository.findTop5ByOrderByReservedCountDesc().stream()
                    .map(m -> MusicalResponse.fromEntity(m, false))
                    .collect(Collectors.toList());
            redis.opsForValue().set(HOT_KEY, top5, TTL);
            return top5;
        }
    }
    
    public SeatResponse getSeatsFromDB(Long musicalId) {
        List<String> seatNames = seatRepository.findByMusical_Id(musicalId).stream()
                .map(Seat::getSeatName)
                .collect(Collectors.toList());
        return SeatResponse.of(musicalId, seatNames);
    }

//    public List<Seat> getSeatsFromDB(Long musicalId) {
//        return seatRepository.findByMusical_Id(musicalId);
//    }

    public SeatResponse getSeatsWithCache(Long musicalId) {
        String key = SEAT_KEY_PREFIX + musicalId;
        Object raw = redis.opsForValue().get(key);

        if (raw != null) {
            return objectMapper.convertValue(raw, SeatResponse.class);
        } else {
            List<Seat> seats = seatRepository.findByMusical_Id(musicalId);
            List<String> seatNames = seats.stream()
                .map(Seat::getSeatName)
                .collect(Collectors.toList());

            SeatResponse seatResponse = SeatResponse.of(musicalId, seatNames);
            redis.opsForValue().set(key, seatResponse, TTL);
            return seatResponse;
        }
    }
}