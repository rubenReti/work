package com.example.notification.service;

import com.example.shared.dto.EmployeeDTO;
import com.example.shared.dto.AuthUserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisNotificationCorrelationStore {
	
	private static final Logger log = LoggerFactory.getLogger(RedisNotificationCorrelationStore.class);
	

    private final NotificationService notificationService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${redis.ttl.minutes}")
    private int redisTtlMinutes; // expire incomplete pairs after X mins - def in app.propp

    private record EventState(
        boolean employeeCreated,
        boolean authCreated,
        EmployeeDTO employee,
        AuthUserDTO authUser
    ) {}

    public synchronized void handleEmployeeCreated(EmployeeDTO dto) {
        String email = dto.getEmail();
        EventState state = getState(email);
        state = new EventState(true, state.authCreated, dto, state.authUser);

        setState(email, state);

        if (state.authCreated) trigger(email, state);
    }

    public synchronized void handleAuthCreated(AuthUserDTO dto) {
        String email = dto.getEmail();
        EventState state = getState(email);
        state = new EventState(state.employeeCreated, true, state.employee, dto);

        setState(email, state);

        if (state.employeeCreated) trigger(email, state);
    }

    private void trigger(String email, EventState state) {
    	log.info("🚨 [Redis] Triggering notification for: " + email);
        notificationService.sendWelcomeEmail(state.employee);
        notificationService.sendCredentialsEmail(state.authUser);
        redisTemplate.delete(key(email));
    }

    private EventState getState(String email) {
        try {
            String json = redisTemplate.opsForValue().get(key(email));
            if (json == null) return new EventState(false, false, null, null);
            return objectMapper.readValue(json, EventState.class);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load EventState from Redis", e);
        }
    }
    
    private String key(String email) {
        return "notify:eventstate:" + email;
    }

    private void setState(String email, EventState state) {
        try {
            String json = objectMapper.writeValueAsString(state);
            redisTemplate.opsForValue().set(key(email), json, Duration.ofMinutes(redisTtlMinutes));
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to save EventState to Redis", e);
        }
    }
}
