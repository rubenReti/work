package com.example.notification.config;

import com.example.shared.event.EmployeeEvent;
import com.example.shared.event.LeaveBalanceEvent;
import com.example.shared.dto.LeaveRequestDTO;
import com.example.shared.event.AuthEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private Map<String, Object> baseConfig(String groupId) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return config;
    }

    // EmployeeEvent consumer
    @Bean
    public ConsumerFactory<String, EmployeeEvent> employeeConsumerFactory() {
        JsonDeserializer<EmployeeEvent> deserializer = new JsonDeserializer<>(EmployeeEvent.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
            baseConfig("notification-employee-group"),
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmployeeEvent> employeeKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmployeeEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(employeeConsumerFactory());
        return factory;
    }

    // AuthEvent consumer
    @Bean
    public ConsumerFactory<String, AuthEvent> authConsumerFactory() {
        JsonDeserializer<AuthEvent> deserializer = new JsonDeserializer<>(AuthEvent.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
            baseConfig("notification-auth-group"),
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuthEvent> authKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AuthEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(authConsumerFactory());
        return factory;
    }
    
    
//leave balance event    
    @Bean
    public ConsumerFactory<String, LeaveBalanceEvent> leaveBalanceEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(LeaveBalanceEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LeaveBalanceEvent> leaveBalanceKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, LeaveBalanceEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(leaveBalanceEventConsumerFactory());
        return factory;
    }
  
    
  //leave request event 
    @Bean
    public ConsumerFactory<String, LeaveRequestDTO> leaveRequestConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "leave-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.shared.dto");

        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            new JsonDeserializer<>(LeaveRequestDTO.class, false)
        );
    }

    @Bean(name = "leaveKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, LeaveRequestDTO> leaveKafkaListenerContainerFactory(
            ConsumerFactory<String, LeaveRequestDTO> leaveRequestConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, LeaveRequestDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(leaveRequestConsumerFactory);
        return factory;
    }



}
