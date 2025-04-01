package com.example.auth.config;

import com.example.shared.event.AuthEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

	@Bean
	public ProducerFactory<String, AuthEvent> authEventProducerFactory() {
	    Map<String, Object> config = new HashMap<>();
	    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
	    return new DefaultKafkaProducerFactory<>(config);
	}

	@Bean
	public KafkaTemplate<String, AuthEvent> authEventKafkaTemplate() {
	    return new KafkaTemplate<>(authEventProducerFactory());
	}

}
