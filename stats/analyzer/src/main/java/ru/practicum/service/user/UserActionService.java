package ru.practicum.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.UserAction;
import ru.practicum.storage.UserActionRepository;

import java.time.Instant;

import static ru.practicum.util.Constants.*;

@Service
@Slf4j
public class UserActionService {
    private final UserActionRepository userActionRepository;

    @Autowired
    public UserActionService(UserActionRepository userActionRepository) {
        this.userActionRepository = userActionRepository;
    }

    public void updateAction(UserActionAvro userActionAvro) {
        long userId = userActionAvro.getUserId();
        long eventId = userActionAvro.getEventId();

        ActionTypeAvro actionTypeAvro = userActionAvro.getActionType();

        double newRating = switch (actionTypeAvro) {
            case VIEW -> WEIGHT_VIEW;
            case REGISTER -> WEIGHT_REGISTER;
            case LIKE -> WEIGHT_LIKE;
        };

        UserAction userAction = userActionRepository.findByUserIdAndEventId(userId, eventId).orElseGet(() -> {
            UserAction newUserAction = new UserAction();
            newUserAction.setUserId(userId);
            newUserAction.setEventId(eventId);
            newUserAction.setRating(0.0);
            newUserAction.setTimestamp(Instant.now());

            return newUserAction;
        });

        Double oldRating = userAction.getRating();

        if (newRating - oldRating < E_10) return;

        userAction.setRating(newRating);
        userAction.setTimestamp(userActionAvro.getTimestamp());

        UserAction newUserAction = userActionRepository.save(userAction);

        log.info("UserAction updated: {}", newUserAction);
    }
}
