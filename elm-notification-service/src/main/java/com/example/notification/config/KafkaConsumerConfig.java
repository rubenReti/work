package com.example.notification.config;

import com.example.shared.event.EmployeeEvent;
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
}
