package com.example.kafka;

import com.example.shared.dto.LeaveRequestDTO;
import com.example.shared.event.EventType;
import com.example.shared.event.LeaveEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class LeaveEventProducer {

    private static final String TOPIC = "leave-events";

    @Autowired
    private KafkaTemplate<String, LeaveEvent> kafkaTemplate;

    public void sendLeaveEvent(EventType type, LeaveRequestDTO dto) {
        LeaveEvent event = LeaveEvent.builder()
                .eventType(type)
                .leave(dto)
                .timestamp(Instant.now())
                .build();

        System.out.println("📤 Sending leave event: " + type + " for " + dto.getEmployeeEmail());
        kafkaTemplate.send(TOPIC, dto.getEmployeeEmail(), event);
    }
}
