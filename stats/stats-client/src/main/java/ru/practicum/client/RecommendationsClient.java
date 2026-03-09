package ru.practicum.client;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.stats.proto.*;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecommendationsClient {
    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client;

    public Stream<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        Iterator<RecommendedEventProto> iterator = null;
        try {
            iterator = client.getRecommendationsForUser(request);
        } catch (StatusRuntimeException e) {
            log.warn("Ошибка при вызове getRecommendationsForUser. \n Код статуса: {} \n Сообщение: {}", e.getStatus(), e.getMessage());
        }

        return asStream(iterator);
    }

    public Stream<RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults) {
        SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        Iterator<RecommendedEventProto> iterator = null;
        try {
            iterator = client.getSimilarEvents(request);
        } catch (StatusRuntimeException e) {
            log.warn("Ошибка при вызове getSimilarEvents. \n Код статуса: {} \n Сообщение: {}", e.getStatus(), e.getMessage());
        }

        return asStream(iterator);
    }

    public Stream<RecommendedEventProto> getInteractionsCount(List<Long> eventIds) {
        InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();

        Iterator<RecommendedEventProto> iterator = null;
        try {
            iterator = client.getInteractionsCount(request);
        } catch (StatusRuntimeException e) {
            log.warn("Ошибка при вызове getInteractionsCount. \n Код статуса: {} \n Сообщение: {}", e.getStatus(), e.getMessage());
        }

        return asStream(iterator);
    }

    private Stream<RecommendedEventProto> asStream(Iterator<RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }
}
