package com.bootcamp.savemypodo.config.redis;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class RedisHttpSessionConfig {
    // 별도 bean 설정 없이도 동작 (spring.data.redis.* 속성 사용)
}