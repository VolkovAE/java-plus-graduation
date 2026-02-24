package ru.practicum.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.client.event.EventClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.exception.ServiceUnavailableException;

import static ru.practicum.util.Constants.ERROR_MESSAGE_EVENT_SERVICE_UNAVAILABLE;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventClientComponent {
    private final EventClient eventRepository;

    public EventFullDto getEventById(Integer eventId) {
        EventFullDto eventFullDto = eventRepository.getEventById(eventId);

        if (eventFullDto.getId() == null) throwEventServiceUnavailable();

        return eventFullDto;
    }

    public void throwEventServiceUnavailable() {
        log.warn(ERROR_MESSAGE_EVENT_SERVICE_UNAVAILABLE);

        throw new ServiceUnavailableException(ERROR_MESSAGE_EVENT_SERVICE_UNAVAILABLE);
    }
}
