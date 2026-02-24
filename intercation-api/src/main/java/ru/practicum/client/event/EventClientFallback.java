package ru.practicum.client.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.event.EventFullDto;

@Component
@Slf4j
public class EventClientFallback implements EventClient {
    @Override
    public EventFullDto getEventById(Integer eventId) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(null);

        return eventFullDto;
    }
}
