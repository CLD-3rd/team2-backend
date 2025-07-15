package com.bootcamp.savemypodo.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://savemypodo-redis.2iapp0.0001.apn2.cache.amazonaws.com:6379");
        return Redisson.create(config);
    }
}