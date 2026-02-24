package ru.practicum.events.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.component.UserClientComponent;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

@Service
@Slf4j
public class EventServiceExtImpl implements EventServiceExt {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserClientComponent userRepository;

    @Autowired
    public EventServiceExtImpl(EventRepository eventRepository,
                               EventMapper eventMapper,
                               UserClientComponent userRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.userRepository = userRepository;
    }

    @Override
    public EventFullDto getEventById(Integer eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + "не найдено."));

        UserDto userDto = userRepository.getUserById(event.getInitiatorId());

        return eventMapper.toEventFullDto(event, userDto);
    }
}
