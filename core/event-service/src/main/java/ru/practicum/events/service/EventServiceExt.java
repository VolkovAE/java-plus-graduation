package ru.practicum.events.service;

import ru.practicum.dto.event.EventFullDto;

public interface EventServiceExt {
    EventFullDto getEventById(Integer eventId);
}
