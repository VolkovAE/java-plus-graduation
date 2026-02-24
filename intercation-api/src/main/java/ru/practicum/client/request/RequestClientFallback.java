package ru.practicum.client.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.request.RequestStatus;
import ru.practicum.storage.ConfirmedCount;

import java.util.List;

@Component
@Slf4j
public class RequestClientFallback implements RequestClient {
    @Override
    public Integer countConfirmedByEventId(Integer eventId) {
        return 0;
    }

    @Override
    public List<ConfirmedCount> countConfirmedForEventIds(List<Integer> eventIds) {
        return List.of();
    }

    @Override
    public Integer countByEventIdAndStatus(Integer eventId, RequestStatus requestStatus) {
        return 0;
    }

    @Override
    public List<ParticipationRequestDto> findByEventId(Integer eventId) {
        return List.of();
    }

    @Override
    public List<ParticipationRequestDto> findAllByIdIn(List<Integer> requestIds) {
        return List.of();
    }

    @Override
    public ParticipationRequestDto update(ParticipationRequestDto participationRequestDto) {
        return null;
    }
}
