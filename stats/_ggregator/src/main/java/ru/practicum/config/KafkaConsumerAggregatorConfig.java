package ru.practicum.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import ru.practicum.deserializer.UserActionDeserializer;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.List;
import java.util.Properties;

import static ru.practicum.util.Constants.BEAN_NAME_CONSUMER_KAFKA_AGGREGATOR;

@Configuration
public class KafkaConsumerAggregatorConfig {
    private final KafkaConsumerProperties kafkaConsumerProperties;

    @Autowired
    public KafkaConsumerAggregatorConfig(KafkaConsumerProperties kafkaConsumerProperties) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
    }

    @Bean(name = BEAN_NAME_CONSUMER_KAFKA_AGGREGATOR)
    @Description(value = "Потребитель сообщений пользователя из кафки (вход в агрегатор)")
    public Consumer<String, UserActionAvro> getConsumerAggregator() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerProperties.host() + ":" + kafkaConsumerProperties.port());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserActionDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.group());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerProperties.offset());

        Consumer<String, UserActionAvro> consumer = new KafkaConsumer<>(config);
        consumer.subscribe(List.of(kafkaConsumerProperties.topic()));

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        return consumer;
    }
}
