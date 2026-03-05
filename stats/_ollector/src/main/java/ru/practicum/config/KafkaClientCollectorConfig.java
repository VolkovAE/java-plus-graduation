package ru.practicum.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import ru.practicum.serializer.GeneralAvroSerializer;

import java.util.Properties;

import static ru.practicum.util.Constants.BEAN_NAME_PRODUCER_KAFKA_COLLECTOR;

@Configuration
public class KafkaClientCollectorConfig {
    private final KafkaProducerCollectorProperties kafkaProducerCollectorProperties;

    @Autowired
    public KafkaClientCollectorConfig(KafkaProducerCollectorProperties kafkaProducerCollectorProperties) {
        this.kafkaProducerCollectorProperties = kafkaProducerCollectorProperties;
    }

    @Bean(name = BEAN_NAME_PRODUCER_KAFKA_COLLECTOR)
    @Description(value = "Продюсер сообщений от пользователя в кафку")
    public Producer<Long, SpecificRecordBase> getProducerCollector() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerCollectorProperties.host() + ":" + kafkaProducerCollectorProperties.port());

        // указываем в качестве сериализатора ключа сообщения — LongSerializer из комплекта kafka-clients
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);

        // указываем в качестве сериализатора данных сообщения наш Avro-сериализатор
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);

        return new KafkaProducer<>(config);
    }
}
