package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.request.RequestStatus;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.storage.ConfirmedCount;
import ru.practicum.storage.RequestRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.util.Constants.FORMATTER;

@Service
@RequiredArgsConstructor
public class RequestServiceExtImpl implements RequestServiceExt {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public Integer countConfirmedByEventId(Integer eventId) {
        return requestRepository.countConfirmedByEventId(eventId);
    }

    @Override
    public List<ConfirmedCount> countConfirmedForEventIds(List<Integer> eventIds) {
        return requestRepository.countConfirmedForEventIds(eventIds);
    }

    @Override
    public Integer countByEventIdAndStatus(Integer eventId, RequestStatus requestStatus) {
        return requestRepository.countByEventIdAndStatus(eventId, requestStatus);
    }

    @Override
    public List<ParticipationRequestDto> findByEventId(Integer eventId) {
        List<Request> requests = requestRepository.findByEventId(eventId);

        return requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public List<ParticipationRequestDto> findAllByIdIn(List<Integer> requestIds) {
        List<Request> requests = requestRepository.findAllByIdIn(requestIds);

        return requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto update(ParticipationRequestDto participationRequestDto) {
        Request requestOld = requestRepository.findById(participationRequestDto.getId())
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + participationRequestDto.getId() + "не найден."));

        requestOld.setRequesterId(participationRequestDto.getRequester());
        requestOld.setEventId(participationRequestDto.getEvent());
        requestOld.setStatus(RequestStatus.valueOf(participationRequestDto.getStatus()));
        requestOld.setCreated(LocalDateTime.parse(participationRequestDto.getCreated(), FORMATTER));
        Request requestNew = requestRepository.save(requestOld);

        return requestMapper.toParticipationRequestDto(requestNew);
    }

    @Override
    public Boolean checkUserParticipation(Integer requesterId, Integer eventId) {
        return requestRepository.existsByRequesterIdAndEventIdAndStatus(requesterId, eventId, RequestStatus.CONFIRMED);
    }

    @Override
    public Map<Integer, Long> getConfirmedRequestsForEvents(List<Integer> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ConfirmedCount> results = requestRepository.countConfirmedForEventIds(eventIds);

        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Integer) (row.getEventId()), // eventId
                        row -> Long.valueOf(row.getCnt()) // count
                ));
    }
}
