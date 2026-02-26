package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.component.EventClientComponent;
import ru.practicum.component.UserClientComponent;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.enums.event.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.storage.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final UserClientComponent userClientComponent;
    private final EventClientComponent eventClientComponent;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Integer userId, Integer eventId) {
        UserDto userDto = userClientComponent.getUserById(userId);

        EventFullDto eventFullDto = eventClientComponent.getEventById(eventId);

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exists");
        }

        // 2. Проверка, что инициатор события не может подать запрос на участие
        if (eventFullDto.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event initiator cannot request participation");
        }

        // 3. Проверка, что событие опубликовано
        if (!eventFullDto.getState().equals(EventState.PUBLISHED.toString())) {
            throw new ConflictException("Cannot participate in unpublished event");
        }

        // 4. Проверка лимита участников
        if (eventFullDto.getParticipantLimit() != 0) {
            long confirmedRequestsCount = requestRepository.countByEventIdAndStatus(eventId, ru.practicum.enums.request.RequestStatus.CONFIRMED);
            if (confirmedRequestsCount >= eventFullDto.getParticipantLimit()) {
                throw new ConflictException("Participant limit reached");
            }
        }
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEventId(eventId);
        request.setRequesterId(userDto.getId());
        if (!eventFullDto.getRequestModeration() || eventFullDto.getParticipantLimit() == 0) {
            request.setStatus(ru.practicum.enums.request.RequestStatus.CONFIRMED);
        } else {
            request.setStatus(ru.practicum.enums.request.RequestStatus.PENDING);
        }

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Integer userId) {
        UserDto userDto = userClientComponent.getUserById(userId);

        List<Request> requests = requestRepository.findByRequesterId(userId);

        return requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Integer userId, Integer requestId) {
        Request request = requestRepository.findByRequesterIdAndId(userId, requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " for user id=" + userId + " was not found"));

        request.setStatus(ru.practicum.enums.request.RequestStatus.CANCELED);

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}
