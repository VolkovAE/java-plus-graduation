package ru.practicum.events.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.RequestStatusUpdateRequest;
import ru.practicum.events.params.AdminEventParams;
import ru.practicum.events.params.PublicEventParams;

import java.util.List;

public interface EventService {
    List<EventFullDto> search(AdminEventParams params);

    EventFullDto updateEventByAdmin(Integer eventId, UpdateEventAdminRequest dto);

    EventFullDto add(Integer userId, NewEventDto newEventDto);

    EventFullDto update(Integer userId, Integer eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto getPublicEventById(Integer eventId, HttpServletRequest request);

    List<EventShortDto> searchPublicEvents(PublicEventParams params, HttpServletRequest request);

    EventRequestStatusUpdateResult changeRequestsStatus(Integer userId, Integer eventId, RequestStatusUpdateRequest updateRequest);

    List<EventShortDto> findAllByUser(Integer userId, int from, int size);

    EventFullDto findByUserAndEvent(Integer userId, Integer eventId);

    List<ParticipationRequestDto> getRequestsByEvent(Integer userId, Integer eventId);
}
