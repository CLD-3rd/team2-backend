package com.bootcamp.savemypodo.service.redis;

import com.bootcamp.savemypodo.config.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
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
        try {
            redisTemplate.opsForValue().set(
                    generateKey(userId),
                    refreshToken,
                    Duration.ofMillis(ttl)
            );
        } catch (Exception e) {
            log.warn("Redis 접근 불가 – 리프레시 토큰 저장 생략: {}", e.toString());
        }
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
