package com.example.notification.kafka;

import com.example.notification.service.NotificationService;
import com.example.shared.dto.LeaveRequestDTO;
import com.example.shared.event.EventType;
import com.example.shared.event.LeaveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeaveEventListener {

    private final NotificationService notificationService;
	private static final Logger log = LoggerFactory.getLogger(LeaveEventListener.class);


    @KafkaListener(
        topics = "leave-events",
        groupId = "notification-service-group",
        containerFactory = "leaveKafkaListenerContainerFactory"
    )
    public void handleLeaveEvent(LeaveEvent event) {
        EventType type = event.getEventType();
        LeaveRequestDTO leave = event.getLeave();
        
        log.info("📥 LeaveEvent received: {}", event);


        if (leave == null) {
            log.warn("⚠️ Received LeaveEvent with null leave payload: " + type);
            return;
        }

        switch (type) {
            case LEAVE_APPROVED -> notificationService.sendLeaveApprovedEmail(leave);
            case LEAVE_REJECTED -> notificationService.sendLeaveRejectedEmail(leave);
            case LEAVE_REQUESTED -> notificationService.sendLeaveRequestedEmail(leave);
            case LEAVE_BALANCE_UPDATED -> notificationService.sendLeaveBalanceUpdatedEmail(leave.getEmployeeEmail());
            default -> log.warn("🔁 LeaveEvent ignored: " + type);
        }
    }
}
