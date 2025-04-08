package com.example.kafka;

import com.example.service.LeaveBalanceService;
import com.example.shared.dto.EmployeeDTO;
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
    private LeaveBalanceService leaveBalanceService;
    
	private static final Logger log = LoggerFactory.getLogger(EmployeeEventListener.class);


	@KafkaListener(
		    topics = "employee-events",
		    groupId = "leave-service-group",
		    containerFactory = "employeeKafkaListenerFactory"
		)
    public void handleEmployeeEvent(EmployeeEvent event) {
    	log.info("📧RRR  Inside handleEmployeeEvent");

        if (event.getEventType() == EventType.EMPLOYEE_CREATED) {
        	log.info("📧HERE RRRR  Inside handleEmployeeEvent");

            leaveBalanceService.initializeBalanceForNewEmployee(event.getEmployee());
        }
    }
}
