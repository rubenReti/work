package com.example.auth.kafka;

import com.example.auth.service.AuthService;
import com.example.shared.dto.AuthUserDTO;
import com.example.shared.event.AuthEvent;
import com.example.shared.event.EventType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuthEventProducer {
	
	private static final Logger log = LoggerFactory.getLogger(AuthEventProducer.class);


    private static final String TOPIC = "auth-events";

    @Autowired
    private KafkaTemplate<String, AuthEvent> kafkaTemplate;

    public void sendAuthUserCreated(AuthUserDTO dto) {
        AuthEvent event = new AuthEvent(EventType.AUTH_USER_CREATED, dto);
        log.info("📤 Sending AUTH_USER_CREATED event for: " + dto.getEmail());
        kafkaTemplate.send(TOPIC, dto.getEmail(), event);
    }
}




//package com.example.auth.kafka;
//
//import com.example.shared.dto.AuthUserDTO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class AuthEventProducer {
//
//    private static final String TOPIC = "auth-events";
//
//    @Autowired
//    private KafkaTemplate<String, AuthUserDTO> kafkaTemplate;
//
//    public void sendAuthUserCreated(AuthUserDTO dto) {
//        System.out.println("📤 Sending AUTH_USER_CREATED event for: " + dto.getEmail());
//        kafkaTemplate.send(TOPIC, dto.getEmail(), dto);
//    }
//}
