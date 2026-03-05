package ru.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.service.CollectorService;
import ru.practicum.stats.proto.UserActionControllerGrpc;
import ru.practicum.stats.proto.UserActionProto;

@GrpcService
@Slf4j
public class UserActionController extends UserActionControllerGrpc.UserActionControllerImplBase {
    private final CollectorService collectorService;

    @Autowired
    public UserActionController(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Получено действие от пользователя: \n{}.", request);

            collectorService.sendUserAction(request);

            log.info("Сообщение передано в топик: \n{}.", request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения от пользователя:  \n{}.", e.getMessage());

            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}
