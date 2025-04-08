package com.example.kafka;

import com.example.shared.event.EmployeeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEventProducer {

    private static final String TOPIC = "employee-events";

    @Autowired
    private KafkaTemplate<String, EmployeeEvent> kafkaTemplate;

    public void sendEvent(EmployeeEvent event) {
        System.out.println("📤 Sending event: " + event.getEventType() + " for employee " + event.getEmployee().getEmail());
        kafkaTemplate.send("employee-events", event);
    }

}
