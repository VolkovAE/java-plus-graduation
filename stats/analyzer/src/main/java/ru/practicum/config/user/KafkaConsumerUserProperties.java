package ru.practicum.config.user;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.analyzer.consumers.user", ignoreUnknownFields = true)
public record KafkaConsumerUserProperties(String host, int port, String offset, String group, String topic) {
}
