package ru.practicum.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.aggregator.consumer", ignoreUnknownFields = true)
public record KafkaConsumerProperties(String host, String port, String offset, String group, String topic) {
}
