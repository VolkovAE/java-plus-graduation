package ru.practicum.service;

import ru.practicum.stats.proto.UserActionProto;

public interface CollectorService {
    void sendUserAction(UserActionProto userAction);
}
