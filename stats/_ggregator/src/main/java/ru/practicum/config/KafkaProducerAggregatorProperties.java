package ru.practicum.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.aggregator.producer", ignoreUnknownFields = true)
public record KafkaProducerAggregatorProperties(String host, String port, String topic) {
}
