package ru.practicum.config.event;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import ru.practicum.deserializer.EventSimilarityDeserializer;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.List;
import java.util.Properties;

import static ru.practicum.util.Constants.BEAN_NAME_CONSUMER_KAFKA_ANALYZER_EVENT;

@Configuration
public class KafkaConsumerAnalyzerEventConfig {
    private final KafkaConsumerEventProperties kafkaConsumerEventProperties;

    @Autowired
    public KafkaConsumerAnalyzerEventConfig(KafkaConsumerEventProperties kafkaConsumerEventProperties) {
        this.kafkaConsumerEventProperties = kafkaConsumerEventProperties;
    }

    @Bean(name = BEAN_NAME_CONSUMER_KAFKA_ANALYZER_EVENT)
    @Description(value = "Потребитель сообщений похожести событий из кафки (вход в аналайзер)")
    public Consumer<String, EventSimilarityAvro> getConsumerAnalyzerEvent() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerEventProperties.host() + ":" + kafkaConsumerEventProperties.port());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventSimilarityDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerEventProperties.group());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerEventProperties.offset());

        Consumer<String, EventSimilarityAvro> consumer = new KafkaConsumer<>(config);
        consumer.subscribe(List.of(kafkaConsumerEventProperties.topic()));

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        return consumer;
    }
}
