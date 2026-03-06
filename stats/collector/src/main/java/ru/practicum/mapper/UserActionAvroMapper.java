package ru.practicum.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.exception.NotFoundException;
import ru.practicum.stats.proto.ActionTypeProto;
import ru.practicum.stats.proto.UserActionProto;

import java.time.Instant;
import java.util.EnumMap;

@Component
@Slf4j
public class UserActionAvroMapper {
    private static final EnumMap<ActionTypeProto, ActionTypeAvro> map = new EnumMap<>(ActionTypeProto.class);

    static {
        map.put(ActionTypeProto.ACTION_VIEW, ActionTypeAvro.VIEW);
        map.put(ActionTypeProto.ACTION_REGISTER, ActionTypeAvro.REGISTER);
        map.put(ActionTypeProto.ACTION_LIKE, ActionTypeAvro.LIKE);
    }

    public static ActionTypeAvro mapToActionTypeAvro(ActionTypeProto actionTypeProto) {
        ActionTypeAvro actionTypeAvro = map.get(actionTypeProto);
        if (actionTypeAvro == null)
            throw new NotFoundException("Не определен тип действия пользователя: " + actionTypeProto.toString());

        return actionTypeAvro;
    }

    public UserActionAvro toUserActionAvro(UserActionProto userActionProto) {
        UserActionAvro userActionAvro = new UserActionAvro();
        userActionAvro.setUserId(userActionProto.getUserId());
        userActionAvro.setEventId(userActionProto.getEventId());
        userActionAvro.setActionType(mapToActionTypeAvro(userActionProto.getActionType()));
        userActionAvro.setTimestamp(Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds(), userActionProto.getTimestamp().getNanos()));

        return userActionAvro;
    }
}
