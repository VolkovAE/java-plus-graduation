package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.util.Constants.*;

@Service
@Slf4j
public class UserActionService {
    private final Map<Long, Map<Long, Double>> eventUserWeightMatrix = new HashMap<>(); // <eventId, Map<userId, max вес>>
    private final Map<Long, Double> eventWeightSums = new HashMap<>();  // <eventId, сумма весов>
    private final Map<Long, Map<Long, Double>> eventMinWeightSums = new HashMap<>();    // <eventIdA, Map<eventIdB, сумма их минимальных весов>>

    public List<EventSimilarityAvro> updateSimilarity(UserActionAvro userActionAvro) {
        long userId = userActionAvro.getUserId();
        long eventId = userActionAvro.getEventId();

        ActionTypeAvro actionTypeAvro = userActionAvro.getActionType();

        List<EventSimilarityAvro> similarities = new ArrayList<>();

        Map<Long, Double> userMaxWeight = eventUserWeightMatrix.computeIfAbsent(eventId, v -> new HashMap<>());
        double oldWeight = userMaxWeight.getOrDefault(userId, 0.0);

        double newWeight = switch (actionTypeAvro) {
            case VIEW -> WEIGHT_VIEW;
            case REGISTER -> WEIGHT_REGISTER;
            case LIKE -> WEIGHT_LIKE;
        };

        if ((newWeight < oldWeight) || (Math.abs(newWeight - oldWeight) < E_10)) return similarities;

        userMaxWeight.put(userId, newWeight);

        eventWeightSums.put(eventId, eventWeightSums.getOrDefault(eventId, 0.0) - oldWeight + newWeight);

        for (Long otherEventId : eventUserWeightMatrix.keySet()) {
            if (eventId == otherEventId) continue;

            Map<Long, Double> otherUserWeights = eventUserWeightMatrix.get(otherEventId);

            if (otherUserWeights != null && otherUserWeights.containsKey(userId)) {
                updateEventMinWeightSums(userId, eventId, otherEventId, oldWeight, newWeight);

                double similarity = 0.0;
                double delitel = Math.sqrt(eventWeightSums.getOrDefault(eventId, 0.0)) * Math.sqrt(eventWeightSums.getOrDefault(otherEventId, 0.0));
                if (Math.abs(delitel) > E_10) similarity = getEventMinWeightSums(eventId, otherEventId) / delitel;

                if (Math.abs(similarity) > E_10) {
                    EventSimilarityAvro eventSimilarityAvro = new EventSimilarityAvro();
                    eventSimilarityAvro.setEventA(Math.min(eventId, otherEventId));
                    eventSimilarityAvro.setEventB(Math.max(eventId, otherEventId));
                    eventSimilarityAvro.setScore(similarity);
                    eventSimilarityAvro.setTimestamp(userActionAvro.getTimestamp());

                    similarities.add(eventSimilarityAvro);
                }
            }
        }

        return similarities;
    }

    private void updateEventMinWeightSums(long userId, long eventId, long otherEventId, double oldWeight, double newWeight) {
        double otherEventWeight = eventUserWeightMatrix.getOrDefault(otherEventId, new HashMap<>()).getOrDefault(userId, 0.0);

        double oldMin = Math.min(oldWeight, otherEventWeight);
        double newMin = Math.min(newWeight, otherEventWeight);
        if (Math.abs(oldMin - newMin) < E_10) return;

        double newSumMin = getEventMinWeightSums(eventId, otherEventId) - oldMin + newMin;

        eventMinWeightSums.computeIfAbsent(Math.min(eventId, otherEventId), e -> new HashMap<>()).put(Math.max(eventId, otherEventId), newSumMin);
    }

    private double getEventMinWeightSums(long eventId, long otherEventId) {
        return eventMinWeightSums.computeIfAbsent(Math.min(eventId, otherEventId), e -> new HashMap<>()).getOrDefault(Math.max(eventId, otherEventId), 0.0);
    }
}
