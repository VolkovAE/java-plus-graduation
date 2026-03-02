package ru.practicum.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.collector", ignoreUnknownFields = true)
public record KafkaProducerCollectorProperties(String host, String port, String topic) {
}
