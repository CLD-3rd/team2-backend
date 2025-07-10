package com.bootcamp.savemypodo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisRefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    // 저장
    public void saveRefreshToken(String userId, String token, long duration, TimeUnit unit) {
        redisTemplate.opsForValue().set("refreshToken:" + userId, token, duration, unit);
    }

    // 조회
    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get("refreshToken:" + userId);
    }

    // 삭제
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete("refreshToken:" + userId);
    }
}
