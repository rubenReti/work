package com.example.notification.kafka;

import com.example.notification.service.NotificationCorrelationStore;
import com.example.notification.service.NotificationService;
import com.example.notification.service.RedisNotificationCorrelationStore;
import com.example.shared.event.AuthEvent;
import com.example.shared.event.EventType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuthEventListener {

    @Autowired
//    private NotificationCorrelationStore correlationStore;
    private RedisNotificationCorrelationStore correlationStore;
    
	private static final Logger log = LoggerFactory.getLogger(AuthEventListener.class);



    @KafkaListener(
        topics = "auth-events",
        groupId = "notification-auth-group",
        containerFactory = "authKafkaListenerFactory"
    )
    public void handleAuthEvent(AuthEvent event) {
        log.info("📥 Received AuthEvent: " + event);

        if (event.getEventType() == EventType.AUTH_USER_CREATED) {
        	correlationStore.handleAuthCreated(event.getUser());
//            correlationStore.handleAuthCreated(event.getUser(), () -> {
//                System.out.println("✅ Both AUTH_USER_CREATED and EMPLOYEE_CREATED received for: " + event.getUser().getEmail());
                // Will trigger NotificationService.send(...)
//            });
        }
    }
}
