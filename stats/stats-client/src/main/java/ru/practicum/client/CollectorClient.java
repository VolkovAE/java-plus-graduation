package ru.practicum.client;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.stats.proto.ActionTypeProto;
import ru.practicum.stats.proto.UserActionControllerGrpc;
import ru.practicum.stats.proto.UserActionProto;

@Component
@Slf4j
public class CollectorClient {
    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub client;

    public void collectUserAction(Long userId, Long eventId, ActionTypeProto action) {
        Timestamp timestamp = Timestamps.fromMillis(System.currentTimeMillis());

        UserActionProto userActionProto = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(action)
                .setTimestamp(timestamp)
                .build();
        try {
            Empty empty = client.collectUserAction(userActionProto);
        } catch (StatusRuntimeException e) {
            log.warn("Ошибка при вызове collectUserAction. \n Код статуса: {} \n Сообщение: {}", e.getStatus(), e.getMessage());
        }
    }
}
