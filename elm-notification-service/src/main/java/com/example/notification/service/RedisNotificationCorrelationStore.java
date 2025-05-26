package com.example.notification.service;

import com.example.shared.dto.EmployeeDTO;
import com.example.shared.dto.AuthUserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisNotificationCorrelationStore {
	
	private static final Logger log = LoggerFactory.getLogger(RedisNotificationCorrelationStore.class);
	
    private final RedissonClient redissonClient;


    private final NotificationService notificationService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    


    @Value("${redis.ttl.minutes}")
    private int redisTtlMinutes; // expire incomplete pairs after X mins - def in app.propp

    private record EventState(
    	    boolean employeeCreated,
    	    boolean authCreated,
    	    boolean leaveBalanceInitialized,
    	    EmployeeDTO employee,
    	    AuthUserDTO authUser
    	) {}

    public synchronized void handleEmployeeCreated(EmployeeDTO dto) {
        log.info("RRR handleEmployeeCreated: " + dto);

        String email = dto.getEmail();
        
        
        //Reddison Lock for critical resource 
        String lockKey = "lock:onboarding:" + email;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                EventState state = getState(email);
                state = new EventState(true, state.authCreated, state.leaveBalanceInitialized, dto, state.authUser);
                setState(email, state);
                if (state.authCreated && state.leaveBalanceInitialized) {
                    trySendIfComplete(email, state);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Lock interrupted for: {}", email);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }        

    public void handleAuthCreated(AuthUserDTO dto) {
        String email = dto.getEmail();
        String lockKey = "lock:onboarding:" + email;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                EventState state = getState(email);
                state = new EventState(state.employeeCreated, true, state.leaveBalanceInitialized, state.employee, dto);
                setState(email, state);
                if (state.employeeCreated && state.leaveBalanceInitialized) {
                    trySendIfComplete(email, state);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Lock interrupted for: {}", email);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    
    public void handleLeaveBalanceInitialized(String email) {
        String lockKey = "lock:onboarding:" + email;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                EventState state = getState(email);
                state = new EventState(
                        state.employeeCreated,
                        state.authCreated,
                        true,
                        state.employee,
                        state.authUser
                );
                setState(email, state);
                trySendIfComplete(email, state);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Lock interrupted for: {}", email);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    
    private void trySendIfComplete(String email, EventState state) {
        if (state.employeeCreated && state.authCreated && state.leaveBalanceInitialized) {
            log.info("📬 [Redis] All onboarding events received for: " + email);
            notificationService.sendWelcomeEmail(state.employee);
            notificationService.sendCredentialsEmail(state.authUser);
            redisTemplate.delete(key(email));
        }
    }



    private EventState getState(String email) {
        try {
            String json = redisTemplate.opsForValue().get(key(email));
            if (json == null) return new EventState(false, false, false, null, null);
            return objectMapper.readValue(json, EventState.class);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load EventState from Redis", e);
            //OR
            //    log.warn("Failed to deserialize state for {}", email, e);
//            return new EventState(false, false, false, null, null);
        }
    }
    
 
    private String key(String email) {
        return "notify:onboarding:" + email;
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
