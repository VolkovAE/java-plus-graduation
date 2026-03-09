package ru.practicum.client.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.request.RequestStatus;
import ru.practicum.storage.ConfirmedCount;

import java.util.List;

import static ru.practicum.util.Constants.*;

@FeignClient(name = NAME_REQUEST_SERVICE, path = PATH_BOX_REQUESTS, fallback = RequestClientFallback.class)
public interface RequestClient {
    @GetMapping(PATH_BOX_REQUEST_CONFIRMED_EVENTS_ID)
    Integer countConfirmedByEventId(@PathVariable @NotNull Integer eventId);

    @GetMapping(PATH_BOX_REQUEST_CONFIRMED_EVENTS_LIST)
    List<ConfirmedCount> countConfirmedForEventIds(@RequestParam(required = false) List<Integer> eventIds);

    @GetMapping(PATH_BOX_REQUEST_STATUS_EVENTS_ID)
    Integer countByEventIdAndStatus(@PathVariable @NotNull Integer eventId,
                                    @RequestParam(required = true) RequestStatus requestStatus);

    @GetMapping(PATH_BOX_REQUEST_EVENTS_ID)
    List<ParticipationRequestDto> findByEventId(@PathVariable @NotNull Integer eventId);

    @GetMapping(PATH_BOX_REQUEST_EVENTS_LIST)
    List<ParticipationRequestDto> findAllByIdIn(@RequestParam(required = false) List<Integer> requestIds);

    @PutMapping
    ParticipationRequestDto update(@RequestBody ParticipationRequestDto participationRequestDto);

    @RequestMapping(value = "/requests/participation/{userId}/{eventId}")
    Boolean checkUserParticipation(@PathVariable Integer userId, @PathVariable Integer eventId);
}
