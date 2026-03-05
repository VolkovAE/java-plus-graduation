package ru.practicum.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.serializer.GeneralAvroSerializer;

import java.util.Properties;

import static ru.practicum.util.Constants.BEAN_NAME_PRODUCER_KAFKA_AGGREGATOR;

@Configuration
public class KafkaClientAggregatorConfig {
    private final KafkaProducerAggregatorProperties kafkaProducerAggregatorProperties;

    @Autowired
    public KafkaClientAggregatorConfig(KafkaProducerAggregatorProperties kafkaProducerAggregatorProperties) {
        this.kafkaProducerAggregatorProperties = kafkaProducerAggregatorProperties;
    }

    @Bean(name = BEAN_NAME_PRODUCER_KAFKA_AGGREGATOR)
    @Description(value = "Продюсер сообщений c похожестью событий в кафку")
    public Producer<String, EventSimilarityAvro> getProducerAggregator() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerAggregatorProperties.host() + ":" + kafkaProducerAggregatorProperties.port());

        // указываем в качестве сериализатора ключа сообщения — LongSerializer из комплекта kafka-clients
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // указываем в качестве сериализатора данных сообщения наш Avro-сериализатор
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);

        return new KafkaProducer<>(config);
    }
}
