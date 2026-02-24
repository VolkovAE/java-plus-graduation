package ru.practicum.client.event;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.event.EventFullDto;

import static ru.practicum.util.Constants.*;

@FeignClient(name = NAME_EVENT_SERVICE, path = PATH_BOX_EVENTS, fallback = EventClientFallback.class)
public interface EventClient {
    @GetMapping(PATH_BOX_EVENTS_ID)
    EventFullDto getEventById(@PathVariable @NotNull Integer eventId);
}
