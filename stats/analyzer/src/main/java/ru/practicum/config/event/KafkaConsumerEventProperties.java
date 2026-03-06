package ru.practicum.config.event;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.analyzer.consumers.event", ignoreUnknownFields = true)
public record KafkaConsumerEventProperties(String host, int port, String offset, String group, String topic) {
}
