package com.bootcamp.savemypodo.service;

import com.bootcamp.savemypodo.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RedisRefreshTokenService {

    @Qualifier("customStringRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    public RedisRefreshTokenService(
            @Qualifier("customStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.redisTemplate = redisTemplate;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 저장
    public void save(Long userId, String refreshToken) {
        long ttl = jwtTokenProvider.getRefreshTokenValidity(); // ms 단위
        redisTemplate.opsForValue().set(
                generateKey(userId),
                refreshToken,
                Duration.ofMillis(ttl)
        );
    }

    // 조회
    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get("refreshToken:" + userId);
    }

    // 삭제
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete("refreshToken:" + userId);
    }

    private String generateKey(Long userId) {
        return "refreshToken:" + userId;
    }
}
