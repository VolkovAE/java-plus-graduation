package ru.practicum.config.user;

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

import static ru.practicum.util.Constants.BEAN_NAME_CONSUMER_KAFKA_ANALYZER_USER;

@Configuration
public class KafkaConsumerAnalyzerUserConfig {
    private final KafkaConsumerUserProperties kafkaConsumerUserProperties;

    @Autowired
    public KafkaConsumerAnalyzerUserConfig(KafkaConsumerUserProperties kafkaConsumerUserProperties) {
        this.kafkaConsumerUserProperties = kafkaConsumerUserProperties;
    }

    @Bean(name = BEAN_NAME_CONSUMER_KAFKA_ANALYZER_USER)
    @Description(value = "Потребитель сообщений пользователя из кафки (вход в аналайзер)")
    public Consumer<String, UserActionAvro> getConsumerAnalyzerUser() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerUserProperties.host() + ":" + kafkaConsumerUserProperties.port());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserActionDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerUserProperties.group());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerUserProperties.offset());

        Consumer<String, UserActionAvro> consumer = new KafkaConsumer<>(config);
        consumer.subscribe(List.of(kafkaConsumerUserProperties.topic()));

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        return consumer;
    }
}
