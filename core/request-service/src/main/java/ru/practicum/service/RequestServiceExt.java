package ru.practicum.service;

import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.request.RequestStatus;
import ru.practicum.storage.ConfirmedCount;

import java.util.List;

public interface RequestServiceExt {
    Integer countConfirmedByEventId(Integer eventId);

    List<ConfirmedCount> countConfirmedForEventIds(List<Integer> eventIds);

    Integer countByEventIdAndStatus(Integer eventId, RequestStatus requestStatus);

    List<ParticipationRequestDto> findByEventId(Integer eventId);

    List<ParticipationRequestDto> findAllByIdIn(List<Integer> requestIds);

    ParticipationRequestDto update(ParticipationRequestDto participationRequestDto);
}
