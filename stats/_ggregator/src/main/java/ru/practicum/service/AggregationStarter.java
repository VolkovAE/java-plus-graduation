package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaProducerAggregatorProperties;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.util.Constants.BEAN_NAME_CONSUMER_KAFKA_AGGREGATOR;
import static ru.practicum.util.Constants.BEAN_NAME_PRODUCER_KAFKA_AGGREGATOR;

@Component
@Slf4j
public class AggregationStarter {
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final Consumer<String, UserActionAvro> consumer;
    private final Producer<String, EventSimilarityAvro> producer;
    private final KafkaProducerAggregatorProperties kafkaProducerAggregatorProperties;
    private final UserActionService userActionService;

    @Autowired
    public AggregationStarter(@Qualifier(BEAN_NAME_CONSUMER_KAFKA_AGGREGATOR) Consumer<String, UserActionAvro> consumer,
                              @Qualifier(BEAN_NAME_PRODUCER_KAFKA_AGGREGATOR) Producer<String, EventSimilarityAvro> producer,
                              KafkaProducerAggregatorProperties kafkaProducerAggregatorProperties,
                              UserActionService userActionService) {
        this.consumer = consumer;
        this.producer = producer;
        this.kafkaProducerAggregatorProperties = kafkaProducerAggregatorProperties;
        this.userActionService = userActionService;
    }

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топик для получения событий от пользователя,
     * формирует похожесть событий и записывает в кафку.
     */
    public void start() {
        try {
            // Цикл обработки событий
            while (true) {
                ConsumerRecords<String, UserActionAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                int count = 0;
                for (ConsumerRecord<String, UserActionAvro> record : records) {
                    // обрабатываем очередную запись
                    handleRecord(record);
                    // фиксируем оффсеты обработанных записей, если нужно
                    manageOffsets(record, count, consumer);
                    count++;
                }
                // фиксируем максимальный оффсет обработанных записей
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от пользователя", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, UserActionAvro> record) {
        try {
            UserActionAvro userActionAvro = record.value();

            List<EventSimilarityAvro> eventSimilarities = userActionService.updateSimilarity(userActionAvro);

            for (EventSimilarityAvro similarityAvro : eventSimilarities) {
                ProducerRecord<String, EventSimilarityAvro> producerRecord = new ProducerRecord<>(
                        kafkaProducerAggregatorProperties.topic(),
                        null,
                        similarityAvro.getTimestamp().toEpochMilli(),
                        similarityAvro.getEventA() + "_" + similarityAvro.getEventB(),
                        similarityAvro
                );

                producer.send(producerRecord);
            }
        } catch (Exception e) {
            log.error("Ошибка обработки записи: {}", record.value(), e);
        }
    }

    private static void manageOffsets(ConsumerRecord<String, UserActionAvro> record, int count, Consumer<String, UserActionAvro> consumer) {
        // обновляем текущий оффсет для топика-партиции
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }
}
