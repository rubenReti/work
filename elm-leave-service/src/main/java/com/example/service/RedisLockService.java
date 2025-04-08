package com.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisLockService {

	
//	 Spring Boot will auto-register:LettuceConnectionFactory And default RedisTemplate<Object, Object> because of application.properties and pom.xml
    private final RedisTemplate<String, String> redisTemplate;
    private final Duration lockTtl = Duration.ofSeconds(15); // lock timeout

    /**
     * Returns lockId if lock was acquired; else null.
     */
    public String tryLock(String key) {
        String lockId = UUID.randomUUID().toString();
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, lockId, lockTtl);
        return Boolean.TRUE.equals(success) ? lockId : null;
    }

    public void releaseLock(String key, String lockId) {
        String current = redisTemplate.opsForValue().get(key);
        if (lockId.equals(current)) {
            redisTemplate.delete(key);
        }
    }
}
