package ru.practicum.controller;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.request.RequestClient;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.request.RequestStatus;
import ru.practicum.service.RequestServiceExt;
import ru.practicum.storage.ConfirmedCount;

import java.util.List;

import static ru.practicum.util.Constants.*;

@RestController
@RequestMapping(PATH_BOX_REQUESTS)
@Validated
@Slf4j
public class RequestControllerBox implements RequestClient {
    private final RequestServiceExt requestService;

    @Autowired
    public RequestControllerBox(@Qualifier("RequestServiceExtImpl") RequestServiceExt requestService) {
        this.requestService = requestService;
    }

    @Override
    @GetMapping(PATH_BOX_REQUEST_CONFIRMED_EVENTS_ID)
    public Integer countConfirmedByEventId(@PathVariable @NotNull Integer eventId) {
        return requestService.countConfirmedByEventId(eventId);
    }

    @Override
    @GetMapping(PATH_BOX_REQUEST_CONFIRMED_EVENTS_LIST)
    public List<ConfirmedCount> countConfirmedForEventIds(@RequestParam(required = false) List<Integer> eventIds) {
        return requestService.countConfirmedForEventIds(eventIds);
    }

    @Override
    @GetMapping(PATH_BOX_REQUEST_STATUS_EVENTS_ID)
    public Integer countByEventIdAndStatus(@PathVariable @NotNull Integer eventId,
                                           @RequestParam(required = true) RequestStatus requestStatus) {
        return requestService.countByEventIdAndStatus(eventId, requestStatus);
    }

    @Override
    @GetMapping(PATH_BOX_REQUEST_EVENTS_ID)
    public List<ParticipationRequestDto> findByEventId(@PathVariable @NotNull Integer eventId) {
        return requestService.findByEventId(eventId);
    }

    @Override
    @GetMapping(PATH_BOX_REQUEST_EVENTS_LIST)
    public List<ParticipationRequestDto> findAllByIdIn(@RequestParam(required = false) List<Integer> requestIds) {
        return requestService.findAllByIdIn(requestIds);
    }

    @PutMapping
    public ParticipationRequestDto update(@RequestBody ParticipationRequestDto participationRequestDto) {
        return requestService.update(participationRequestDto);
    }
}
