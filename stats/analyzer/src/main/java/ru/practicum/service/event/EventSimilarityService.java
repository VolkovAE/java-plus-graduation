package ru.practicum.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;
import ru.practicum.storage.EventSimilarityRepository;

@Service
@Slf4j
public class EventSimilarityService {
    private final EventSimilarityRepository eventSimilarityRepository;

    @Autowired
    public EventSimilarityService(EventSimilarityRepository eventSimilarityRepository) {
        this.eventSimilarityRepository = eventSimilarityRepository;
    }

    public void updateSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        long eventA = eventSimilarityAvro.getEventA();
        long eventB = eventSimilarityAvro.getEventB();

        Long eventId1 = Math.min(eventA, eventB);
        Long eventId2 = Math.max(eventA, eventB);

        EventSimilarity eventSimilarity = eventSimilarityRepository.findByEventId1AndEventId2(eventId1, eventId2).orElseGet(() -> {
            EventSimilarity newEventSimilarity = new EventSimilarity();
            newEventSimilarity.setEventId1(eventId1);
            newEventSimilarity.setEventId2(eventId2);
            newEventSimilarity.setSimilarity(eventSimilarityAvro.getScore());
            newEventSimilarity.setTimestamp(eventSimilarityAvro.getTimestamp());

            return eventSimilarityRepository.save(newEventSimilarity);
        });

        boolean isSave = false;

        if (!eventSimilarity.getSimilarity().equals(eventSimilarityAvro.getScore())) {
            isSave = true;
            eventSimilarity.setSimilarity(eventSimilarityAvro.getScore());
        }

        if (!eventSimilarity.getTimestamp().equals(eventSimilarityAvro.getTimestamp())) {
            isSave = true;
            eventSimilarity.setTimestamp(eventSimilarityAvro.getTimestamp());
        }

        if (isSave) eventSimilarity = eventSimilarityRepository.save(eventSimilarity);

        log.info("Save EventSimilarity: {}", eventSimilarity);
    }
}
