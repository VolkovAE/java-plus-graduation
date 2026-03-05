package ru.practicum.service.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.UserAction;
import ru.practicum.stats.proto.InteractionsCountRequestProto;
import ru.practicum.stats.proto.RecommendedEventProto;
import ru.practicum.stats.proto.SimilarEventsRequestProto;
import ru.practicum.stats.proto.UserPredictionsRequestProto;
import ru.practicum.storage.EventSimilarityRepository;
import ru.practicum.storage.UserActionRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class RecommendationsService {
    private final EventSimilarityRepository eventSimilarityRepository;
    private final UserActionRepository userActionRepository;

    public RecommendationsService(EventSimilarityRepository eventSimilarityRepository, UserActionRepository userActionRepository) {
        this.eventSimilarityRepository = eventSimilarityRepository;
        this.userActionRepository = userActionRepository;
    }

    public Stream<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        Long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        List<Long> userEventIds = userActionRepository.findRecentEventIdsByUserId(userId, PageRequest.of(0, maxResults));
        if (userEventIds.isEmpty()) return Stream.empty();

        Set<Long> userEventSet = new HashSet<>(userEventIds);
        List<EventSimilarity> similarities = eventSimilarityRepository.findSimilarEventsForListEventIds(userEventIds);

        Map<Long, Double> recommendations = new HashMap<>();
        for (EventSimilarity sim : similarities) {
            Long otherEventId = userEventSet.contains(sim.getEventId1()) ? sim.getEventId2() : sim.getEventId1();
            if (!userEventSet.contains(otherEventId))
                recommendations.merge(otherEventId, sim.getSimilarity(), Math::max);
        }

        return recommendations.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(maxResults)
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build());
    }

    public Stream<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        Long eventId = request.getEventId();
        Long userId = request.getUserId();

        Set<Long> userEvents = userActionRepository.findEventIdsByUserId(userId);

        List<EventSimilarity> similarities = eventSimilarityRepository.findAllByEventId(eventId);

        Map<Long, Double> recommendations = new HashMap<>();
        for (EventSimilarity sim : similarities) {
            if (!(userEvents.contains(sim.getEventId1()) && userEvents.contains(sim.getEventId2()))) {
                Long otherEventId = sim.getEventId1().equals(eventId) ? sim.getEventId2() : sim.getEventId1();
                recommendations.merge(otherEventId, sim.getSimilarity(), Math::max);
            }
        }

        return recommendations.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(request.getMaxResults())
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build());
    }

    public Stream<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        List<Long> eventIds = request.getEventIdList();
        if (eventIds.isEmpty()) return Stream.empty();

        List<UserAction> userActions = userActionRepository.findActionsForListEventIds(eventIds);
        if (userActions.isEmpty()) return Stream.empty();

        Map<Long, Double> sums = userActions.stream()
                .collect(Collectors.groupingBy(
                        UserAction::getEventId,
                        Collectors.summingDouble(UserAction::getRating)
                ));

        return sums.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build());
    }
}
