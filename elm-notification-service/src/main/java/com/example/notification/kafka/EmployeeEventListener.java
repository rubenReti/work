package com.example.notification.kafka;

import com.example.notification.service.NotificationCorrelationStore;
import com.example.notification.service.NotificationService;
import com.example.notification.service.RedisNotificationCorrelationStore;
import com.example.shared.event.EmployeeEvent;
import com.example.shared.event.EventType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEventListener {

    @Autowired
//    private NotificationCorrelationStore correlationStore;
    private RedisNotificationCorrelationStore correlationStore;

	private static final Logger log = LoggerFactory.getLogger(EmployeeEventListener.class);


    @KafkaListener(
        topics = "employee-events",
        groupId = "notification-employee-group",
        containerFactory = "employeeKafkaListenerFactory"
    )
    public void handleEmployeeEvent(EmployeeEvent event) {
    	
        log.info("Received EmployeeEvent: {}", event);


        if (event.getEventType() == EventType.EMPLOYEE_CREATED) {
        	
//            System.out.println("🔍 [EmployeeEventListener] EMPLOYEE_CREATED for: " + event.getEmployee().getEmail());

            	correlationStore.handleEmployeeCreated(event.getEmployee());

//            correlationStore.handleEmployeeCreated(event.getEmployee(), () -> {
//                System.out.println("✅ Both EMPLOYEE_CREATED and AUTH_USER_CREATED received for: " + event.getEmployee().getEmail());
                // Will trigger NotificationService.send(...)
//            });
        }
        else 	{	System.out.println("⚠️ [EmployeeEventListener] Ignored non-creation event: " + event.getEventType());   
        		}
    }
}
