package com.example.notification.kafka;

import com.example.notification.service.RedisNotificationCorrelationStore;
import com.example.shared.event.EventType;
import com.example.shared.event.LeaveBalanceEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LeaveBalanceEventListener {
	
	private static final Logger log = LoggerFactory.getLogger(LeaveBalanceEventListener.class);


	  @Autowired
	    private RedisNotificationCorrelationStore correlationStore;

	  @KafkaListener(
			    topics = "leave-balance-events",
			    groupId = "notification-service-group",
			    containerFactory = "leaveBalanceKafkaListenerContainerFactory"
			)
    public void handleLeaveBalanceEvent(LeaveBalanceEvent event) {
    	log.info("📥 LeaveBalanceEvent received: {}", event);

        if (event.getEventType() == EventType.LEAVE_BALANCE_INITIALIZED) {
        	log.info("📥HERE  LeaveBalanceEvent received: {}", event);

            correlationStore.handleLeaveBalanceInitialized(event.getEmail());
        }
    }
}
