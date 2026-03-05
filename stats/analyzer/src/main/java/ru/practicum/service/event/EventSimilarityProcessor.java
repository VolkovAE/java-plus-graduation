package ru.practicum.service.event;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static ru.practicum.util.Constants.BEAN_NAME_CONSUMER_KAFKA_ANALYZER_EVENT;

@Component
@Slf4j
public class EventSimilarityProcessor implements Runnable {
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final Consumer<String, EventSimilarityAvro> consumer;
    private final EventSimilarityService eventSimilarityService;

    @Autowired
    public EventSimilarityProcessor(@Qualifier(BEAN_NAME_CONSUMER_KAFKA_ANALYZER_EVENT) Consumer<String, EventSimilarityAvro> consumer,
                                    EventSimilarityService eventSimilarityService) {
        this.consumer = consumer;
        this.eventSimilarityService = eventSimilarityService;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Цикл обработки событий
                ConsumerRecords<String, EventSimilarityAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                int count = 0;
                for (ConsumerRecord<String, EventSimilarityAvro> record : records) {
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
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, EventSimilarityAvro> record) {
        EventSimilarityAvro eventSimilarityAvro = record.value();

        eventSimilarityService.updateSimilarity(eventSimilarityAvro);
    }

    private static void manageOffsets(ConsumerRecord<String, EventSimilarityAvro> record, int count, Consumer<String, EventSimilarityAvro> consumer) {
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
