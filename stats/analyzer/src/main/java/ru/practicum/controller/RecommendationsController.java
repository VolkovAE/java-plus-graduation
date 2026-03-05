package ru.practicum.controller;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.service.recommendation.RecommendationsService;
import ru.practicum.stats.proto.*;

@GrpcService
@Slf4j
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final RecommendationsService recommendationsService;

    @Autowired
    public RecommendationsController(RecommendationsService recommendationsService) {
        this.recommendationsService = recommendationsService;
    }

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            // Получаем поток рекомендаций
            recommendationsService.getRecommendationsForUser(request)
                    .forEach(responseObserver::onNext);
            // Завершаем поток
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при получении рекомендаций для пользователя {}", request.getUserId(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Ошибка при получении рекомендаций: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            // Получаем поток мероприятий
            recommendationsService.getSimilarEvents(request)
                    .forEach(responseObserver::onNext);
            // Завершаем поток
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при получении мероприятий, похожих на {}, для пользователя {}",
                    request.getEventId(), request.getUserId(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Ошибка при получении мероприятий: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            // Получаем поток с суммой максимальных весов
            recommendationsService.getInteractionsCount(request)
                    .forEach(responseObserver::onNext);
            // Завершаем поток
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при получении максимальных весов для списка мероприятий", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Ошибка при получении максимальных весов: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}
