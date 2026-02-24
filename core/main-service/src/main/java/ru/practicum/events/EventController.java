package ru.practicum.events;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.event.EventClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.events.service.EventServiceExt;

import static ru.practicum.util.Constants.PATH_BOX_EVENTS;
import static ru.practicum.util.Constants.PATH_BOX_EVENTS_ID;

@RestController
@RequestMapping(PATH_BOX_EVENTS)
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventController implements EventClient {
    private final EventServiceExt eventService;

    @Override
    @GetMapping(PATH_BOX_EVENTS_ID)
    public EventFullDto getEventById(@PathVariable @NotNull Integer eventId) {
        return eventService.getEventById(eventId);
    }
}
