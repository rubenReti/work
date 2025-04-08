package com.example.kafka;

import com.example.service.LeaveBalanceService;
import com.example.shared.event.EventType;
import com.example.shared.event.LeaveBalanceEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class LeaveBalanceEventProducer {

    private static final String TOPIC = "leave-balance-events";

    @Autowired
    private KafkaTemplate<String, LeaveBalanceEvent> kafkaTemplate;
    
	private static final Logger log = LoggerFactory.getLogger(LeaveBalanceEventProducer.class);


    public void send(EventType type, String email) {
    	log.info("📤 RRR inside LeaveBalanceEventProducer sende: " + type + " for " + email);

        LeaveBalanceEvent event = LeaveBalanceEvent.builder()
                .eventType(type)
                .email(email)
                .timestamp(Instant.now())
                .build();

        System.out.println("📤 LeaveBalanceEvent: " + type + " for " + email);
    	log.info("📤 LeaveBalanceEvent: " + type + " for " + email);

        kafkaTemplate.send(TOPIC, email, event);
    }
}
