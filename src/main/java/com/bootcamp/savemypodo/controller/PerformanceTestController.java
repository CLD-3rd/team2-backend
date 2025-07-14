package com.bootcamp.savemypodo.controller;

import com.bootcamp.savemypodo.dto.musical.MusicalResponse;
import com.bootcamp.savemypodo.entity.Seat;
import com.bootcamp.savemypodo.service.PerformanceTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Profile("performance")
@RestController
@RequestMapping("/api/test/performance")
@RequiredArgsConstructor
public class PerformanceTestController {

    private final PerformanceTestService service;

    // 1. 전체 공연 DB에서 직접 조회
    @GetMapping("/musicals/all")
    public List<MusicalResponse> getAllMusicalsFromDB() {
        return service.getAllFromDB();
    }

    // 2. 인기 top5 캐시 + 전체 DB 조회
    @GetMapping("/musicals/mixed")
    public List<MusicalResponse> getMixedMusicals() {
        return service.getMixedWithCache();
    }

    // 3. 인기 top5 캐시만 조회
    @GetMapping("/musicals/top5")
    public List<MusicalResponse> getTop5FromCache() {
        return service.getOnlyTop5FromCache();
    }

    // 4. 공연 좌석 캐시 조회
    @GetMapping("/seats/{musicalId}/cached")
    public List<Seat> getSeatsWithCache(@PathVariable Long musicalId) {
        return service.getSeatsWithCache(musicalId);
    }

    // 5. 공연 좌석 DB 조회
    @GetMapping("/seats/{musicalId}/db")
    public List<Seat> getSeatsFromDB(@PathVariable Long musicalId) {
        return service.getSeatsFromDB(musicalId);
    }
}