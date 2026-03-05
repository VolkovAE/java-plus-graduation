package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.config.KafkaProducerCollectorProperties;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.mapper.UserActionAvroMapper;
import ru.practicum.stats.proto.UserActionProto;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static ru.practicum.util.Constants.BEAN_NAME_PRODUCER_KAFKA_COLLECTOR;

@Service
@Slf4j
public class CollectorServiceImpl implements CollectorService {
    private final Producer<Long, SpecificRecordBase> producer;
    private final UserActionAvroMapper userActionAvroMapper;
    private final KafkaProducerCollectorProperties kafkaProducerCollectorProperties;

    @Autowired
    public CollectorServiceImpl(@Qualifier(BEAN_NAME_PRODUCER_KAFKA_COLLECTOR) Producer<Long, SpecificRecordBase> producer,
                                UserActionAvroMapper userActionAvroMapper,
                                KafkaProducerCollectorProperties kafkaProducerCollectorProperties) {
        this.producer = producer;
        this.userActionAvroMapper = userActionAvroMapper;
        this.kafkaProducerCollectorProperties = kafkaProducerCollectorProperties;
    }

    @Override
    public void sendUserAction(UserActionProto userActionProto) {
        UserActionAvro userActionAvro = userActionAvroMapper.toUserActionAvro(userActionProto);

        ProducerRecord<Long, SpecificRecordBase> record = new ProducerRecord<>(kafkaProducerCollectorProperties.topic(),
                null,
                userActionAvro.getTimestamp().toEpochMilli(),
                userActionAvro.getEventId(),
                userActionAvro);

        Future<RecordMetadata> future = producer.send(record);
        producer.flush();
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Ошибка при отправке сообщения от пользователя в топик {}.", e.getMessage());
        }
    }
}
