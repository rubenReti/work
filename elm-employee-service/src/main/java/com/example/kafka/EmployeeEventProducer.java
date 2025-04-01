package com.example.kafka;

import com.example.shared.event.EmployeeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEventProducer {

	private static final String MAIN_TOPIC = "employee-events";
	// Kafka retains messages by default for only 7 days - we can exdend it by a bean later 
	private static final String ARCHIVE_TOPIC = "employee-events-archive";
	

    @Autowired
    private KafkaTemplate<String, EmployeeEvent> kafkaTemplate;

    public void sendEvent(EmployeeEvent event) {
        System.out.println("📤 Sending event to MAIN topic: " + event.getEventType() + " for " + event.getEmployee().getEmail());
        kafkaTemplate.send(MAIN_TOPIC, event);

        System.out.println("🗃 Archiving event to ARCHIVE topic: " + event.getEventType());
        kafkaTemplate.send(ARCHIVE_TOPIC, event);
    }


}
